# AI 実装ルール（Firebase AI Logic / Gemini）

店舗情報の自動生成に Firebase AI Logic（Agent Platform バックエンド）を利用する際の実装規約。
背景・料金・コスト管理の詳細は `docs/vertex-ai-cost-management.md`、
バックエンド API の移行経緯は `docs/firebase-ai-backend-migration.md` を参照。

## 基本方針

- **バックエンドは Agent Platform**（`GenerativeBackend.agentPlatform(...)`）を使う。
  Google Cloud の従量課金で動作するため、プリペイドクレジット枯渇で停止する
  googleAI() バックエンドは採用しない。
  なお旧 `GenerativeBackend.vertexAI(...)` は firebase-ai 17.16.0（firebase-bom 34.18.0）で
  deprecated となり `agentPlatform(...)` が後継。enum は別値（`GenerativeBackendEnum.AGENT_PLATFORM`）
  になるが 17.16.0 時点では VERTEX_AI と同一分岐でリクエスト先も同じ。詳細は
  `docs/firebase-ai-backend-migration.md`（コンソール設定への影響の判定を含む）を参照。
- **モデルは `gemini-3.1-flash-lite`**。Gemini 3.x は Firebase AI Logic で
  **`location = "global"` のみ対応**のため、`agentPlatform(location = "global")` を明示指定する
  （`agentPlatform()` のデフォルトも "global" だが、要件であることを明示するため指定する）。
  モデル名はサーバ側の値で SDK では検証できない。変更時は Firebase の対応モデル一覧で
  モデル名とロケーションを必ず確認する。
- 実装は `ShopAiDataSource`（androidMain の Kotlin 実装。プラットフォーム固有）に置く。
  プラットフォーム対応の詳細は `.claude/rules/platform-specific.md` を参照。

## コスト削減（必須）

`generationConfig` に必ず以下を設定する。

- **`thinkingConfig { thinkingBudget = 0 }`（思考の無効化）**
  - 抽出・分類のような単純タスクでは思考トークンは不要。思考トークンは出力単価で課金される
    ため、無効化が最大のコスト削減になる。
  - 複雑な多段推論が必要な新機能を追加する場合のみ、`-1`（動的）や正の値を検討する。
- **`maxOutputTokens`（出力上限）を設定する**
  - ⚠️ 絞りすぎると JSON が途中で切れて parse に失敗し空データが返る。ユーザーの再実行を
    誘発してかえって呼び出しが増えるため、想定出力に**余裕を持たせた値**にする（現状 512）。

## 構造化出力

- `responseMimeType = "application/json"` ＋ `responseSchema`（`Schema.obj(...)`）で
  出力スキーマを固定する。列挙は `Schema.enumeration(...)` を使う。
- パース失敗時は例外を投げず、空のドメインモデル（例: `ShopAiInfo()`）を返して握りつぶさない
  設計にする（UI 側でリトライ可能にする）。

## キャッシュ（呼び出し回数の削減）

- 同一入力の生成結果は Room（`shop_ai_cache`）にキャッシュする。
- UseCase は **AI 呼び出し前にキャッシュを参照**し、ヒット時は API を叩かず返す。
  ミス時のみ呼び出し、**成功時のみ**保存する（エラーはキャッシュしない）。
- ローカル生成できる値（例: Google マップ検索 URL）はキャッシュに依存せず毎回生成する。

## 多重実行ガード

- ViewModel の生成トリガーは、実行中（`RunStatus.Loading`）の再呼び出しを弾く。
  ボタン連打による重複リクエスト（＝無駄な課金）を防ぐ。

## App Check（前提）

- App Check は ENFORCED。**デバッグビルドを実機/エミュで動かす場合**は、起動時に logcat へ
  出力されるデバッグトークンを Firebase コンソール（App Check → デバッグトークン）に登録しないと
  AI 呼び出しがブロックされる。デバイスごとに別トークン。

## 変更時の同期

- モデル・ロケーション・スキーマを変更したら、iOS 側の実装（Swift）にも同等の変更が必要か確認する
  （`.claude/rules/platform-specific.md`）。
