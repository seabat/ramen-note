# Vertex AI（Firebase AI Logic）コスト削減 実施記録

ramen-note の店情報自動生成機能（`ShopAiDataSource` 経由の Gemini 呼び出し）に対して行った
コスト削減の**実施記録**。方針検討の経緯と、実際に反映した変更・未実施項目をまとめる。

- 実施日: 2026-08-29
- 対象: `ShopAiDataSource.kt`（Firebase AI Logic / `GenerativeBackend.vertexAI()`）
- 想定利用: 100 ユーザー × 月10回 = **1,000 回/月**
- SDK: firebase-bom 34.12.0（firebase-ai 17.11.0 に解決）

---

## 1. 実施結果サマリ

| # | 項目 | 状態 | 対応 |
|---|---|---|---|
| 1 | 思考トークン無効化（`thinkingBudget = 0`） | ✅ 実施済み | `ShopAiDataSource.kt` |
| 2 | モデル移行（`gemini-3.1-flash-lite` / `location=global`） | ✅ 実施済み | `ShopAiDataSource.kt`（実機での生成確認は別途推奨） |
| 3 | 出力上限（`maxOutputTokens = 512`） | ✅ 実施済み | `ShopAiDataSource.kt` |
| 4 | 多重実行ガード | ✅ 実施済み | `AddShopViewModel.kt` |
| 6 | 失敗時リトライ制御 | ✅ 対応不要 | 現状 auto-retry なし（`catch` のみ）で要件充足 |
| 5 | 同一店キャッシュ再利用（Room） | ✅ 実施済み | DB v6→v7 マイグレーション＋新テーブル `shop_ai_cache` |
| 7 | Firebase App Check | ✅ 実施済み | 2アプリ登録＋`firebaseml.googleapis.com` が `ENFORCED`（gcloud で確認済み） |
| 8 | 予算キャップ引上げ（¥10→¥100） | ✅ 実施済み | 利用額上限を ¥10→¥100（enforcement 維持）に更新し停止解除 |

---

## 2. 実施した変更（コード）

### 2-1. 思考OFF ＋ 出力上限 — `ShopAiDataSource.kt`

`generationConfig` に以下を追加。

```kotlin
generationConfig {
    responseMimeType = "application/json"
    responseSchema = jsonSchema
    // 思考トークンの課金を抑えるため思考を無効化（単純な抽出・分類タスクのため）
    thinkingConfig {
        thinkingBudget = 0
    }
    // description の長文化による出力トークン増を上限で抑制
    maxOutputTokens = 512
}
```

- `thinkingBudget = 0` は firebase-ai 17.11.0 の `ThinkingConfig` で「思考を無効化」する正規の指定（SDK ドキュメント記載）。**最大のコスト削減ポイント**。
- `maxOutputTokens = 512` は JSON が途中で切れて parse 失敗しないよう余裕を持たせた値。

### 2-3. モデル移行 — `ShopAiDataSource.kt`

`gemini-2.5-flash`（2026/10 廃止予定・$0.30/$2.50）から、より安価な
`gemini-3.1-flash-lite`（$0.25/$1.50）へ移行。Gemini 3.x は Firebase AI Logic では
`global` ロケーションのみ対応のため、バックエンドにも `location = "global"` を明示指定した。

```kotlin
Firebase.ai(backend = GenerativeBackend.vertexAI(location = "global")).generativeModel(
    modelName = "gemini-3.1-flash-lite",
    ...
)
```

### 2-4. 同一店キャッシュ再利用（Room） — 新規テーブル `shop_ai_cache`

同一 (areaName, shopName) の生成結果を Room に保持し、再生成時は Vertex AI を叩かず
キャッシュを返す。呼び出し回数そのものを削減し、暴走時のコスト増も抑える。

- 新規: `ShopAiCacheEntity`（PK: areaName + shopName）/ `ShopAiCacheDao` /
  `ShopAiCacheRepository`(Contract+実装)
- DB: `RamenNoteDatabase` を **v6 → v7** に更新、`MIGRATION_6_7`（`shop_ai_cache` を
  CREATE、既存テーブルは不変＝追加のみで低リスク）
- `FetchAiShopInfoUseCase`: 先頭でキャッシュを参照し、ヒット時は AI 呼び出しをスキップ
  （`mapUrl` はローカル生成のため毎回再生成）。ミス時のみ AI を呼び、成功時に保存。
- DI: `repositoryModule` に `ShopAiCacheRepositoryContract` を登録、UseCase の引数を追加

```kotlin
// FetchAiShopInfoUseCase 冒頭
val cachedAiInfo = shopAiCacheRepository.get(areaName, shopName)
if (cachedAiInfo != null) {
    return RunStatus.Success(cachedAiInfo.copy(mapUrl = createMapUrl(areaName, shopName)))
}
```

### 2-2. 多重実行ガード — `AddShopViewModel.kt`

`fetchShopAiInfo` の冒頭に、生成中の再実行を弾くガードを追加。

```kotlin
override fun fetchShopAiInfo(areaName: String, shopName: String) {
    // 多重実行ガード: 生成中に再度呼ばれても API を叩かない（ボタン連打による重複課金を防ぐ）
    if (_shopAiInfoState.value is RunStatus.Loading) return
    viewModelScope.launch {
        _shopAiInfoState.value = RunStatus.Loading()
        ...
    }
}
```

---

## 3. コスト効果（1,000 回/月・$1=¥150 換算）

1 回あたりのトークン概算：入力 ~700 / 出力（可視）~200 / 思考 現状~1,000。

| 項目 | ①変更前<br>2.5-flash・思考ON | ②中間<br>2.5-flash・思考OFF | ③今回の実施後（最終）<br>3.1-flash-lite・思考OFF |
|---|---|---|---|
| モデル単価（入力/出力 /1M） | $0.30 / $2.50 | $0.30 / $2.50 | $0.25 / $1.50 |
| 思考トークン | ON | **OFF** | **OFF** |
| maxOutputTokens | なし | **512** | **512** |
| 入力コスト | $0.21 | $0.21 | $0.18 |
| 出力コスト | $0.50 | $0.50 | $0.30 |
| 思考コスト | **$2.50** | $0.00 | $0.00 |
| **月間合計（USD）** | **$3.21** | $0.71 | **$0.48** |
| **月間合計（円）** | **約 ¥482** | 約 ¥107 | **約 ¥71** |
| 変更前比 | — | ▲78% | **▲85%** |

**今回の実施（①→③）で 約 ¥482 → ¥71（▲85%）**。思考OFF＋出力上限＋モデル移行をすべて反映済み。

---

## 4. 検証結果

- ✅ `./gradlew :androidApp:assembleDebug` → **BUILD SUCCESSFUL**（`thinkingConfig` DSL・`maxOutputTokens`・`vertexAI(location="global")`・モデル名・ガードのコンパイル確認）
- ✅ ktlint: 変更ファイルに違反なし（`ktlintCheck` の失敗は無関係な既存 iosMain ファイルの pre-existing 違反）
- ✅ Room KSP コード生成成功（新 Entity/DAO/`MIGRATION_6_7` を含めてビルド通過）
- ⏳ 実機での動作確認は未実施 → **実行推奨**（ビルドでは検証不可）:
  - `gemini-3.1-flash-lite` / `global` ロケーションでの疎通・分類精度・description 品質・出力上限512
  - キャッシュのヒット/ミス動作（2回目の同一店で API を叩かないこと）
  - 既存 DB（v6）からの v7 マイグレーションが例外なく完了すること

---

## 5. 未実施・残作業

### コード（別タスク）
- **（推奨）モデル名の Remote Config 化**: 今回モデルを `gemini-3.1-flash-lite` に移行済み。将来の廃止・値下げに際しアプリ更新なしで差し替えられるよう、Firebase Remote Config でモデル名を管理する構成が公式推奨。

### 手動作業（コンソール）— 実施済み（2026-08-30）
- **⑦ App Check**: 適用済み。Android/iOS の2アプリが「登録済み（適用済み）」、App Check サービス `firebaseml.googleapis.com`（Firebase AI Logic / Vertex AI 対応）の enforcementMode が `ENFORCED` であることを gcloud（Firebase App Check REST API）で確認。
- **⑧ 予算キャップ**: 利用額上限「ramen-note 利用額上限」を **¥10 → ¥100**（利用額上限の適用＝enforcement 維持）に更新。¥10 発動によるサービス停止を解除。通知しきい値は 50/80/100%（¥50/¥80/¥100）に自動更新。
  - 注意: 実コスト想定 ~¥71/月に対し ¥100 は余裕が約1.4倍と小さい。停止が頻発する場合は ¥300〜500 へ再調整する。
  - 補足: 「利用額上限の適用（spending cap）」はプレビュー機能で Cloud Billing Budget API には現れない（コンソール管理）。CLI で読めるのはアラート予算のみ（`Firebase Project seabat-dev`=¥50、`ramen-note 予算`=¥1）。

---

## 6. 検証で潰すべき注意点

- ⚠️ `maxOutputTokens` を絞りすぎると JSON が途中で切れて parse 失敗 → `ShopAiInfo()` 返却 → ユーザー再実行 → かえって呼び出し増。512 で切れないか実機確認する。
- ⚠️ `gemini-3.1-flash-lite` は Gemini 3.x のため `location = "global"` が必須（設定済み）。今後さらに新しいモデルへ移行する際も Firebase の対応一覧でモデル名とロケーションを再確認する。
- ⚠️ キャッシュ導入時は TTL（永続再利用 or 手動再生成許可）を UX 判断する。

---

## 参考

- [Firebase AI Logic Pricing](https://firebase.google.com/docs/ai-logic/pricing)
- [Firebase AI Logic Thinking](https://firebase.google.com/docs/ai-logic/thinking)
- [Gemini pricing in 2026 (CloudZero)](https://www.cloudzero.com/blog/gemini-pricing/)
