# ramen-note プロジェクト

## 概要
KMP（Kotlin Multiplatform）のラーメン店管理アプリ。Android / iOS 対応、Compose Multiplatform を使用。

## 言語・コミュニケーション
- コード内コメント・コミットメッセージ・PR・ドキュメントはすべて日本語で記述する

## 技術スタック
Compose Multiplatform（UI）/ Room（DB）/ Koin（DI）/ Ktor（HTTP）/ Coil（画像）/
Navigation Compose（画面遷移）/ SKIE（iOS 連携）/ compose-nav-graph（NavGraph Graph ビュー）/
Firebase（Analytics・Crashlytics・AI Logic・App Check）/ Gemini AI（Android のみ）

- **バージョンは `gradle/libs.versions.toml` が唯一の正**。本ファイルには転記しない
  （同期されず陳腐化するため）。必要時にカタログを読むこと
- **Android SDK**: compileSdk 37, minSdk 24, targetSdk 37
  （`minSdk 24` は API ガードの要否を左右するため、ここに記載する）
- **上げられないバージョンとその理由** → `.claude/rules/dependencies.md`

## 実行コマンド
```bash
./gradlew :androidApp:assembleDebug              # ビルド
./gradlew ktlintCheck                            # lint チェック
./gradlew ktlintFormat                           # lint 自動修正
./gradlew :sharedLogic:allTests :sharedUI:allTests  # テスト（123件）
./gradlew :sharedUI:generatePreviewGallery       # NavGraph Previews 生成（68件）
```

## プロジェクト構造

```
ramen-note/
├── sharedLogic/   # KMP ライブラリ: data / domain / config 層
│   └── src/
│       ├── commonMain/   # DB(Room), Repository, UseCase, API クライアント
│       ├── androidMain/  # Android 固有実装
│       └── iosMain/      # iOS 固有実装
├── sharedUI/      # Compose Multiplatform ライブラリ: ui / di 層
│   └── src/commonMain/   # 全画面・コンポーネント・ナビゲーション
├── androidApp/    # Android アプリエントリーポイント（Application クラス等）
├── iosApp/        # iOS アプリ（Swift / Xcode プロジェクト）
└── gradle/        # バージョンカタログ（libs.versions.toml）
```

- `sharedLogic` / `sharedUI` 間の依存: `sharedUI` → `sharedLogic`

## アーキテクチャ
クリーンアーキテクチャ + MVVM の3層構造: `UI → Domain → Data`

- 各層は **Contract（インターフェース）** を介して依存する（実装クラスを直接参照しない）

## 詳細ルール（必要時に参照）
以下は状況に応じて該当ファイルを読むこと（コンテキスト節約のため本ファイルには自動展開しない）。

- **コーディング規約 / ファイル命名規則** → `.claude/rules/coding-conventions.md`
  （ViewModel / UseCase / Repository / DataSource / Room の実装規約、命名パターン）
- **@Preview / NavGraph アノテーション規約** → `.claude/rules/navgraph-preview.md`
  （Composable の Preview import・`@NavDestination` / `@NavPreview` 付与ルール）
- **DI（Koin）登録ルール** → `.claude/rules/di-koin.md`
  （各モジュールの登録先ファイルと登録方法）
- **プラットフォーム固有 API** → `.claude/rules/platform-specific.md`
  （expect/actual、Contract + Swift の2パターン。片方変更時の同期）
- **機密情報（API キー等）の管理** → `.claude/rules/secrets.md`
  （local.properties / BuildSecrets 生成 / 編集ブロック対象）
- **ktlint 設定** → `.claude/rules/ktlint.md`
  （無効化ルール、lint 対象外の範囲）
- **PR フォーマット** → `.claude/rules/pr-format.md`
  （タイトル・本文の構成）
- **AI 実装（Firebase AI Logic / Gemini）** → `.claude/rules/ai-implementation.md`
  （Agent Platform バックエンド・モデル/ロケーション・思考OFF/出力上限・キャッシュ・App Check）
- **依存バージョン管理** → `.claude/rules/dependencies.md`
  （固定中のバージョンと理由、更新手順、iOS を含む検証コマンド）

## サブエージェントの自動起動
実装中、以下の変更を行ったら対応するサブエージェントを起動すること（PostToolUse Hook でも該当時にリマインダが出る）。
- **画面（`*Screen.kt`）を作成・大きく変更したら** → `ui-ux-designer` で UI/UX（Material Design 3 準拠・アクセシビリティ等）を確認
- **LazyColumn を含む画面（`*Screen.kt`）を変更したら** → `regression-reviewer` でデグレ確認（インデックスベースの自動スクロール等が壊れやすいため）
- **`.claude/` 配下（agents / skills / settings.json 等）または `build.gradle.kts` を変更したら** → `readme-updater` で README を最新化

## Hooks
自動動作の詳細は @.claude/settings.json を参照。
PostToolUse: Edit/Write 後に変更ファイルを判定し、上記サブエージェントの起動を促すリマインダを注入する。

## 注意事項
- Room の KSP 生成タスクと Compose Resource 生成タスクに依存関係がある（build.gradle.kts 参照）
- iOS ビルドは Xcode から実行（`iosApp/` ディレクトリ）
