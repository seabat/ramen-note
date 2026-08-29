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
| 3 | 出力上限（`maxOutputTokens = 512`） | ✅ 実施済み | `ShopAiDataSource.kt` |
| 4 | 多重実行ガード | ✅ 実施済み | `AddShopViewModel.kt` |
| 6 | 失敗時リトライ制御 | ✅ 対応不要 | 現状 auto-retry なし（`catch` のみ）で要件充足 |
| 2 | モデル移行（`gemini-3.1-flash-lite`） | ⏳ 未実施 | 廃止（2026年10月）前に最新モデル名を確認して別途実施 |
| 5 | 同一店キャッシュ再利用（Room） | ⏳ 未実施 | DB マイグレーション v6→v7 を伴うため別タスク |
| 7 | Firebase App Check | ⏳ 未実施（手動） | Firebase コンソール作業 |
| 8 | 予算キャップ引上げ（¥10→¥500〜1,000） | ⏳ 未実施（手動） | GCP コンソール作業 |

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

| 項目 | ①変更前<br>2.5-flash・思考ON | ②今回の実施後<br>2.5-flash・思考OFF | ③（将来）移行後<br>3.1-flash-lite・思考OFF |
|---|---|---|---|
| モデル単価（入力/出力 /1M） | $0.30 / $2.50 | $0.30 / $2.50 | $0.25 / $1.50 |
| 思考トークン | ON | **OFF** | **OFF** |
| maxOutputTokens | なし | **512** | **512** |
| 入力コスト | $0.21 | $0.21 | $0.18 |
| 出力コスト | $0.50 | $0.50 | $0.30 |
| 思考コスト | **$2.50** | $0.00 | $0.00 |
| **月間合計（USD）** | **$3.21** | **$0.71** | $0.48 |
| **月間合計（円）** | **約 ¥482** | **約 ¥107** | 約 ¥71 |
| 変更前比 | — | **▲78%** | ▲85% |

**今回の実施（①→②）で 約 ¥482 → ¥107（▲78%）**。③のモデル移行は将来の廃止対応時に併せて実施予定。

---

## 4. 検証結果

- ✅ `./gradlew :androidApp:assembleDebug` → **BUILD SUCCESSFUL**（`thinkingConfig` DSL・`maxOutputTokens`・ガードのコンパイル確認）
- ✅ ktlint: 変更した 2 ファイルに違反なし（`ktlintCheck` の失敗は無関係な既存 iosMain ファイルの pre-existing 違反）
- ⏳ 実機での分類精度・description 品質の目視確認（思考OFF・出力上限512の影響）は未実施 → 実行推奨

---

## 5. 未実施・残作業

### コード（別タスク）
- **② モデル移行**: `modelName = "gemini-2.5-flash"` のまま。2.5系は 2026年10月廃止予定のため、廃止前に Firebase の最新モデル一覧で `gemini-3.1-flash-lite` 等の正式名を確認して差し替える。モデル名はサーバ側の値で SDK からは検証不可のため、憶測での差し替えは行わなかった。
- **⑤ 同一店キャッシュ**: 同一 (areaName, shopName) の生成結果を Room に保持し再生成時に API を叩かない仕組み。DB マイグレーション（現行 v6 → v7）と DAO/Repository 追加を伴うため、デグレ確認込みの別タスクとして分離。

### 手動作業（コンソール）
- **⑦ App Check 有効化**: Firebase コンソールで App Check を有効化し、正規アプリ以外からの Vertex AI 呼び出しを遮断（実質的な乱用防止・レート制限の土台）。
- **⑧ 予算キャップ引上げ**: GCP「予算とアラート」で上限 ¥10 → ¥500〜1,000 に引上げ（enforcement 維持）。実コスト ~¥107/月に対し ¥10 は低すぎ、月半ばで機能停止するため必須。

---

## 6. 検証で潰すべき注意点

- ⚠️ `maxOutputTokens` を絞りすぎると JSON が途中で切れて parse 失敗 → `ShopAiInfo()` 返却 → ユーザー再実行 → かえって呼び出し増。512 で切れないか実機確認する。
- ⚠️ モデル移行時は最新モデル名を Firebase の対応一覧で再確認する。
- ⚠️ キャッシュ導入時は TTL（永続再利用 or 手動再生成許可）を UX 判断する。

---

## 参考

- [Firebase AI Logic Pricing](https://firebase.google.com/docs/ai-logic/pricing)
- [Firebase AI Logic Thinking](https://firebase.google.com/docs/ai-logic/thinking)
- [Gemini pricing in 2026 (CloudZero)](https://www.cloudzero.com/blog/gemini-pricing/)
