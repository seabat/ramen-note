# Firebase AI Logic バックエンド移行記録（`vertexAI()` → `agentPlatform()`）

Firebase AI Logic のバックエンド指定 API を、deprecated となった `GenerativeBackend.vertexAI()` から
後継の `GenerativeBackend.agentPlatform()` へ移行した記録。

- 実施日: 2026-08-30
- 対象: `sharedLogic/src/androidMain/.../data/datasource/ShopAiDataSource.kt`
- 契機: バージョンカタログ更新
  （firebase-bom **34.12.0 → 34.18.0** / firebase-ai **17.11.0 → 17.16.0**）
- コスト削減施策そのものの記録は [`vertex-ai-cost-management.md`](./vertex-ai-cost-management.md) を参照

---

## 1. 背景

firebase-ai 17.16.0 で `GenerativeBackend.vertexAI(location: String = "us-central1")` が
deprecated になった。コンパイル時の警告は以下。

```
'fun vertexAI(location: String = ...): GenerativeBackend' is deprecated.
Use agentPlatform instead. Note that agentPlatform default location is "global"
while vertexAI was "us-central1".
```

「Vertex AI」ブランドが「Agent Platform」へ改称されたことに伴う API 名の移行で、
機能の変更ではない。

## 2. 変更内容

```kotlin
// 移行前
Firebase.ai(backend = GenerativeBackend.vertexAI(location = "global"))
// 移行後
Firebase.ai(backend = GenerativeBackend.agentPlatform(location = "global"))
```

`location` は移行前から `"global"` を明示指定しているため（`gemini-3.1-flash-lite` が
Gemini 3.x で `global` のみ対応のため必須）、デフォルト値の違い
（`vertexAI` = `us-central1` / `agentPlatform` = `global`）の影響は受けない。

## 3. SDK 挙動の検証

`GenerativeBackendEnum` は `VERTEX_AI` と `AGENT_PLATFORM` の**別の enum 値**になるため、
一見すると接続先が変わるように読める。実際には firebase-ai 17.16.0 のバイトコードを確認した
結果、**両者を区別している箇所は存在しない**。

`FirebaseAI` のモデルパス組み立て（`WhenMappings` により VERTEX_AI→1 / AGENT_PLATFORM→2 /
GOOGLE_AI→3 にマップされる）:

```
tableswitch { 1: 60,    // VERTEX_AI
              2: 60,    // AGENT_PLATFORM  ← 同じ分岐
              3: 115 }  // GOOGLE_AI       ← ここだけ別分岐
```

`APIController` の WebSocket URL 組み立ても同様に `1: 68 / 2: 68 / 3: 104` で、
VERTEX_AI と AGENT_PLATFORM は常に同一分岐に落ちる。

結果として、送信される HTTP リクエストは移行前後で一致する。

| 項目 | 移行前 `vertexAI("global")` | 移行後 `agentPlatform("global")` |
|---|---|---|
| ホスト | `firebasevertexai.googleapis.com` | 同左 |
| パス | `projects/{projectId}/locations/global/publishers/google/models/gemini-3.1-flash-lite` | 同左 |
| モデル | `gemini-3.1-flash-lite` | 同左 |
| 課金経路 | Vertex AI の従量課金 | 同左 |

※ 別分岐になるのは `googleAI()`（`GOOGLE_AI`）のみで、こちらは
`projects/{projectId}/models/{model}` を組み立てる。本アプリは使用していない。

---

## 4. コンソール設定への影響

**結論: Firebase コンソール・Google Cloud コンソールともに変更は不要。**

サーバーに届くリクエストが移行前と同一である以上、サーバー側の設定（API の有効化・
App Check の適用・課金と予算）が移行によって影響を受けることはない。

| 対象 | 変更要否 | 根拠 |
|---|---|---|
| Firebase コンソール → AI Logic（API プロバイダ） | **不要** | 引き続き Vertex AI Gemini API 側のエンドポイントを使用。Gemini Developer API への切り替えではない |
| Firebase コンソール → App Check（適用対象・ENFORCED） | **不要** | 保護対象のサービス・エンドポイントが不変。既存の `firebaseml.googleapis.com` = `ENFORCED` がそのまま有効 |
| Firebase コンソール → App Check（デバッグトークン） | **不要** | 登録済みトークンはデバイス単位で有効。ただし新しい端末/エミュで動かす場合は従来どおり登録が必要 |
| Google Cloud コンソール → 利用額上限（¥100） | **不要** | 対象サービス `Vertex AI (aiplatform.googleapis.com)` に **Agent Platform が含まれる**とコンソールに明記（下記参照） |
| Google Cloud コンソール → 予算アラート | **不要** | 同上 |
| Google Cloud コンソール → API の有効化 | **不要** | 呼び出すサービス（`firebasevertexai.googleapis.com`）が不変 |

### 利用額上限が Agent Platform を対象に含むこと（確認済み・2026-08-30）

利用額上限「ramen-note Vetex AI の利用額上限」（¥100）の範囲は
**プロジェクト `ramen-note` × サービス `Vertex AI (aiplatform.googleapis.com)`** で、
請求先アカウント全体ではなくサービス単位で絞られている。そのため
「Agent Platform が Vertex AI の対象に含まれるか」が移行の可否を左右するが、
Google Cloud コンソールの予算編集画面に次の注記があり、対象に含まれることが確認できる。

> Vertex AI サービスのこの費用の上限は、オンデマンド費用にのみ適用され、
> Gemini Enterprise app、**Agent Platform**、Antigravity が含まれます。

本アプリの利用は従量課金（オンデマンド）のため「オンデマンド費用にのみ適用」の条件も満たす。
よって `agentPlatform()` 移行後も ¥100 の上限はそのまま有効で、設定変更は不要。

> ⚠️ 「利用額上限の適用（spending cap）」は**プレビュー機能**のため、
> 対象サービスの内訳が将来変更される可能性がある。Vertex AI 周辺のブランド再編が
> 続いているため、AI 関連の SDK / モデルを更新した際はこの注記を再確認する。

### 確認したい場合のコマンド

App Check の適用状態は Firebase App Check REST API で確認できる（読み取りのみ）。

```bash
curl -s -H "Authorization: Bearer $(gcloud auth print-access-token)" \
  "https://firebaseappcheck.googleapis.com/v1/projects/<PROJECT_ID>/services"
```

`firebaseml.googleapis.com` の `enforcementMode` が `ENFORCED` であれば移行前と同じ状態。

> ℹ️ 「利用額上限の適用（spending cap）」はプレビュー機能のため Cloud Billing Budget API には
> 現れず、コンソールでの目視確認が必要（`vertex-ai-cost-management.md` の 5 章参照）。

---

## 5. 影響を受けないもの

[`vertex-ai-cost-management.md`](./vertex-ai-cost-management.md) に記録したコスト削減施策は
すべてバックエンド指定に依存しないため、内容は変更なし。

- **思考OFF（`thinkingBudget = 0`）／出力上限（`maxOutputTokens = 512`）**: `generationConfig` の設定
- **Room キャッシュ（`shop_ai_cache`）／多重実行ガード**: アプリ側の実装

### コスト（1,000 回/月・$1=¥150 換算）

1 回あたりのトークン概算：入力 ~700 / 出力（可視）~200 / 思考 OFF（0）。

| 項目 | 移行前 `vertexAI("global")` | 移行後 `agentPlatform("global")` |
|---|---|---|
| モデル | `gemini-3.1-flash-lite` | 同左 |
| モデル単価（入力/出力 /1M） | $0.25 / $1.50 | $0.25 / $1.50 |
| 思考トークン | OFF | OFF |
| `maxOutputTokens` | 512 | 512 |
| 入力コスト | $0.18 | $0.18 |
| 出力コスト | $0.30 | $0.30 |
| 思考コスト | $0.00 | $0.00 |
| **月間合計（USD）** | **$0.48** | **$0.48** |
| **月間合計（円）** | **約 ¥71** | **約 ¥71** |
| 差分 | — | **±0** |

モデル・単価・トークン量・課金される SKU がいずれも同一のため、**移行によるコスト変化はない**。

> ℹ️ ここに載せているのは「バックエンド移行前後」の比較。
> `gemini-2.5-flash` → `gemini-3.1-flash-lite` の**モデル移行**による削減
> （約 ¥482 → ¥71・▲85%）は [`vertex-ai-cost-management.md`](./vertex-ai-cost-management.md) の
> 3 章を参照。

---

## 6. 残作業

- ⬜ **実機での AI 生成スモークテスト**: リクエスト内容が同一のため回帰の可能性は低いが、
  firebase-ai を 17.11.0 → 17.16.0 とマイナー 5 つ分更新しているため、
  店舗情報の自動生成を 1 回実行して確認する。
  - App Check が ENFORCED のため、デバッグビルドで動かす場合は
    logcat に出るデバッグトークンの登録が必要（端末ごと）。

---

## 7. 今後の注意点

- `agentPlatform()` は `AGENT_PLATFORM` という独立した enum 値を持つため、
  **将来の SDK バージョンで VERTEX_AI と挙動が分岐する可能性がある**。
  firebase-ai を更新した際は、本ドキュメント 3 章と同じ手順（`FirebaseAI` /
  `APIController` の分岐確認）で同一性を再確認するか、リリースノートを確認する。
- モデル・ロケーションを変更する場合は、iOS 側の実装（Swift）にも同等の変更が必要か確認する
  （[`.claude/rules/platform-specific.md`](../.claude/rules/platform-specific.md)）。

---

## 参考

- [Firebase AI Logic ドキュメント](https://firebase.google.com/docs/ai-logic)
- [Firebase App Check REST API — services](https://firebase.google.com/docs/reference/appcheck/rest/v1/projects.services)
- [`vertex-ai-cost-management.md`](./vertex-ai-cost-management.md) — コスト削減の実施記録
- [`.claude/rules/ai-implementation.md`](../.claude/rules/ai-implementation.md) — AI 実装のコーディングルール
