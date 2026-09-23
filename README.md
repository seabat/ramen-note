# RamenNote

ラーメン店の情報を記録・管理するための Kotlin Multiplatform アプリケーションです。

## 概要

RamenNote は、ラーメン店の情報をエリア別に管理し、訪問予定や食レポを記録できるアプリです。Android と iOS の両方のプラットフォームで動作します。

## スクリーンショット

<div align="center">
  <img src="docs/AppMockUpStudio/home.png" width="200" alt="スクリーンショット1">
  <img src="docs/AppMockUpStudio/schedule.png" width="200" alt="スクリーンショット2">
  <img src="docs/AppMockUpStudio/shop.png" width="200" alt="スクリーンショット3">
  <img src="docs/AppMockUpStudio/history.png" width="200" alt="スクリーンショット4">
</div>

## ダウンロード

このアプリは以下のストアからダウンロードできます。

<div align="center">
  <a href="https://play.google.com/store/apps/details?id=jp.seabatlab.ramennote">
    <img src="https://play.google.com/intl/en_us/badges/static/images/badges/ja_badge_web_generic.png" alt="Google Play で手に入れよう" style="height: 60px; vertical-align: bottom;">
  </a>
  <a href="https://apps.apple.com/jp/app/%E3%83%A9%E3%83%BC%E3%83%A1%E3%83%B3note/id6754216932">
    <img src="https://tools.applemediaservices.com/api/badges/download-on-the-app-store/black/ja-jp?size=250x83&releaseDate=1276560000" alt="App Store からダウンロード" style="height: 60px; vertical-align: bottom;">
  </a>
</div>

## 主な機能

- **ホーム画面**: 最近の食レポやお気に入り店を一覧表示
- **予定管理**: ラーメンを食べに行く予定を管理
- **ノート**: エリア別に店舗情報を登録・編集・管理
- **食レポ**: 訪問した店のレビューと写真を記録
- **設定**: アプリの各種設定

## 技術スタック

### フレームワーク・ライブラリ

- **Kotlin Multiplatform**: クロスプラットフォーム開発
- **Compose Multiplatform**: UI フレームワーク
- **Room**: ローカルデータベース
- **Koin**: 依存性注入
- **Ktor**: HTTP クライアント
- **Coil**: 画像読み込み
- **Navigation Compose**: 画面遷移
- **Google Maps Compose**: 店舗位置の地図表示（Android。iOS は UIKitView + MapKit）。座標変換は Geocoding API（Ktor 経由）
- **SKIE**: Kotlin/Swift 連携（iOS）
- **compose-nav-graph**: IDE の NavGraph Graph ビュー（ナビゲーション構造の可視化）
- **Firebase**: Analytics・Crashlytics・AI・App Check（Android: Play Integrity / Debug、iOS: DeviceCheck / Debug）
- **Gemini AI**: AI 機能（Android）

### 開発環境

- Kotlin: 2.3.21
- Compose Multiplatform: 1.12.0
- Android Gradle Plugin: 9.3.2
- Gradle: 9.7.1（AGP 9.3 以降は Gradle 9.5 以上が必須）
- Android SDK: minSdk 24, targetSdk 37, compileSdk 37
- AGP 9 の新 DSL を採用（`android.newDsl` / `android.builtInKotlin` を有効化）。
  Kotlin のコンパイルは AGP 組み込みのものを使うため、`org.jetbrains.kotlin.android`
  プラグインは適用していません

## プロジェクト構造

```
ramen-note/
├── sharedLogic/             # KMP ライブラリ: data / domain / config 層
│   └── src/
│       ├── commonMain/      # 共通コード（Room・Repository・UseCase・API クライアント）
│       ├── androidMain/     # Android 固有実装
│       └── iosMain/         # iOS 固有実装
├── sharedUI/                # Compose Multiplatform ライブラリ: ui / di 層（commonMain 中心）
├── androidApp/              # Android アプリのエントリーポイント
├── iosApp/                  # iOS アプリ（Swift / Xcode）
└── gradle/                  # バージョンカタログ（libs.versions.toml）
```

### 主要なパッケージ構成

- `sharedLogic` の `data/`: データソース、リポジトリ、Room データベース
- `sharedLogic` の `domain/`: ドメインモデル、ユースケース
- `sharedUI` の `ui/`: 画面、コンポーネント、ナビゲーション
- `sharedUI` / `sharedLogic` の `di/`: 依存性注入（Koin）の設定

## セットアップ

### 必要な環境

- JDK 11 以上
- Android Studio または IntelliJ IDEA
- Xcode (iOS ビルドの場合)

### Git Hooks の設定

**クローン後に一度だけ以下を実行して有効化してください**（事前に Claude Code CLI の `claude` コマンドと `jq` が PATH に通っている必要があります）。

```bash
git config core.hooksPath .githooks
```

**仕組み**: `git commit` のたびに `.githooks/pre-commit` が自動実行され、ktlint 整形・
`rules-reviewer` によるコーディング規約準拠レビュー・`regression-reviewer` によるデグレ再発防止
レビューを行います。ステージに `*.kt` が含まれる場合はまず整形し、差分が出れば再ステージした上で
commit を中止します（差分がなければそのままレビューへ進みます）。`rules-reviewer` は対象ファイル
（`coding-conventions` / `di-koin` / `navgraph-preview` / `platform-specific` / `ai-implementation` /
`secrets`）が含まれる場合のみ、`regression-reviewer` は対象ファイル（`HistoryScreen.kt`、または
LazyColumn・onCompleted を含む `*Screen.kt`）が含まれる場合のみ、現在ログイン中のセッションで
`claude -p` を実行して行い、FAIL があれば commit を中止します（レビュー自体が実行できなかった場合も
安全側に倒して中止）。緊急時に全体を飛ばす場合は `git commit --no-verify` を使用してください。

### Unsplash API の設定

**ビルド前に以下を設定してください。**

1. [Unsplash Developers](https://unsplash.com/developers) でアプリケーションを登録し、Access Key を取得
2. プロジェクトルートの `local.properties` に以下を追加

```properties
UNSPLASH_ACCESS_KEY=取得した Access Key
```

**仕組み**: ビルド時に Gradle が `local.properties` から値を読み込み、commonMain 向けに
`BuildSecrets.kt` を自動生成します。アプリコードは `BuildSecrets.UNSPLASH_ACCESS_KEY` 経由で参照します
（エリア画像の表示に使用）。

**注意**: Access Key を設定せずにビルドすると、エリア画像の取得が正常に動作しません。

### Google Maps API の設定

**ビルド前に以下を設定してください。**

1. Google Cloud Console で対象プロジェクトの「Maps SDK for Android」「Geocoding API」を有効化し、API キーを発行
2. プロジェクトルートの `local.properties` に以下を追加

```properties
GOOGLE_MAPS_API_KEY=取得した API キー
```

**仕組み**: Unsplash と同様に、ビルド時に Gradle が `local.properties` から値を読み込み、commonMain
向けに `BuildSecrets.GOOGLE_MAPS_API_KEY` を自動生成するほか、Android の `AndroidManifest.xml` にも
`manifestPlaceholders` 経由で埋め込まれます（店舗位置の地図表示・住所→座標変換に使用）。

**注意**: API キーを設定せずにビルドすると、店舗位置マップ機能が動作しません。

### Firebase App Check の設定

デバッグビルドを実機/エミュレータで動かす場合、**以下の手順でデバッグトークンを登録してください。**

1. アプリを起動し、デバッグトークンを確認する（Android は logcat、iOS は Xcode コンソールに出力される）
2. Firebase コンソール → App Check → デバッグトークン に登録する（デバイスごとに別トークン）

**仕組み**: Firebase App Check が不正クライアントからの API アクセスを防いでおり（本番ビルドは
Android: Play Integrity・iOS: DeviceCheck、デバッグビルドは両OSともDebugプロバイダー）、
ENFORCED（強制）で有効化されているため、上記の登録なしにはデバッグビルドから保護対象 API を
呼び出せません。詳細は [`docs/firebase-api-security.md`](./docs/firebase-api-security.md) を参照してください。

### Firebase AI Logic（Gemini）の設定

店舗情報の自動生成（エリア名と店名から Web サイト・最寄り駅・カテゴリ・紹介文を AI が生成）に
**Firebase AI Logic** を利用しています。**追加のローカル設定は不要です**（上記の Firebase App Check
のデバッグトークン登録が済んでいれば動作します）。

**仕組み**: バックエンドは Vertex AI / Agent Platform（`GenerativeBackend.agentPlatform(location = "global")`）、
モデルは `gemini-3.1-flash-lite`、実装は `sharedLogic` の `ShopAiDataSource`（androidMain）/
`FetchAiShopInfoUseCase` です。課金は Vertex AI の従量課金（Firebase Blaze プランに紐づく Cloud
Billing、利用額上限設定済み）。コスト削減のため思考トークンの無効化・出力トークン上限・Room
キャッシュ（同一 (エリア, 店名) は再生成時に API を叩かない）を実施しています。

> 💡 料金体系・コスト管理の方針は [`docs/vertex-ai-cost-management.md`](./docs/vertex-ai-cost-management.md)、
> バックエンド API の移行経緯は [`docs/firebase-ai-backend-migration.md`](./docs/firebase-ai-backend-migration.md)、
> AI 実装のコーディングルールは [`.claude/rules/ai-implementation.md`](./.claude/rules/ai-implementation.md) を参照してください。

### ビルドと実行

#### Android アプリ

macOS/Linux:
```bash
./gradlew :androidApp:assembleDebug
```

Windows:
```bash
.\gradlew.bat :androidApp:assembleDebug
```

#### iOS アプリ

1. Xcode で `/iosApp` ディレクトリを開く
2. Xcode から実行するか、IDE の実行設定を使用

### テスト

テストは `sharedLogic` / `sharedUI` の `commonTest` にあります（計 123 件）。

```bash
./gradlew :sharedLogic:allTests :sharedUI:allTests
```

> **注意**: `:androidApp:testDebugUnitTest` は **NO-SOURCE**（実行対象なし）で、
> 何もテストせずに成功します。テストの実行には必ず `allTests` を使用してください。

### コードスタイル (ktlint)

- チェック:  
  ```bash
  ./gradlew ktlintCheck
  ```
- フォーマット:  
  ```bash
  ./gradlew ktlintFormat
  ```

### NavGraph Graph ビュー

compose-nav-graph プラグイン（0.2.1）を導入しており、IDE の **NavGraph Graph** タブでナビゲーション構造を視覚的に確認できます。

プレビューギャラリーの生成:
```bash
./gradlew :sharedUI:generatePreviewGallery
```

> **注意**: `@Preview` アノテーションは `androidx.compose.ui.tooling.preview.Preview` を使用してください。`org.jetbrains` 版は Compose Multiplatform 1.9.0 で deprecated となり、compose-nav-graph の KSP プロセッサが認識しません。

## Claude Code

このプロジェクトは [Claude Code](https://claude.ai/code) による AI 支援開発に対応しています。

### プロジェクト設定（CLAUDE.md）

`.claude/CLAUDE.md` にアーキテクチャ・コーディング規約・DI 登録ルール・PR フォーマットなどのプロジェクト固有の指示を記載しています。Claude Code はこのファイルを自動的に読み込み、プロジェクトのルールに従った提案・実装を行います。

### スキル（Skills）

Claude Code のカスタムスキルを `.claude/skills/` に定義しています。

| スキル                       | 説明                                                                                                                                         |
|-----------------------------|----------------------------------------------------------------------------------------------------------------------------------------------|
| `/android-ui-operator`      | ramen-note の Android アプリを実機・エミュレータで操作し、動作確認や実装後の結合テストを行う。ビルド手順・画面遷移マップ・機能別の動作確認レシピは `recipe.md` に記載。UI 要素のレイアウト取得・座標特定、タップ・テキスト入力・スワイプ・キーイベント送出を Android CLI と adb 経由で実行する |
| `/icon-replacer`            | Android・iOS 両プラットフォームのアプリアイコン・スプラッシュスクリーン・タスクスイッチャーオーバーレイを一括で差し替える。開発者が `content_image`・`transparent_image`・`bg_color` を用意し、明示的に実行する |
| `/ios-ui-operator`          | ramen-note の iOS アプリをシミュレータ・実機で操作し、動作確認や実装後の結合テストを行う。ビルド手順・画面遷移マップ・機能別の動作確認レシピは `recipe.md` に記載。UI 階層取得・タップ・テキスト入力は Maestro MCP（`maestro mcp`）経由で実行する（詳細は後述） |
| `/readme-updater`           | ramen-note の README.md をプロジェクトの実態と同期させる。エージェント・スキル・Hooks・技術スタックのいずれかが変更されたとき、またはユーザーが明示的に依頼したときに実行する |
| `/regression-reviewer`      | 過去に発生したデグレの再発防止チェックリスト（HistoryScreen.kt 自動スクロール、RunStatus.Success 完了コールバックの LaunchedEffect ラップ）に現在の差分が抵触していないかレビューする。指摘・修正案の提示のみ行い、修正自体はユーザー承認後に別途実施する。`git commit` 時に対象ファイルがあれば pre-commit フックから自動実行される（詳細は後述） |
| `/release-prep`             | リリース前の準備作業。現在ブランチと main のバージョン比較・確認 → 前回リリース差分の把握 → ストア向けリリースノートの作成・保存。バージョンの更新自体は `/version-increment` に委譲する |
| `/rules-reviewer`           | `.claude/rules/` のコーディング規約（coding-conventions / di-koin / navgraph-preview / platform-specific / ai-implementation / secrets）に現在の差分が準拠しているかレビューする。指摘・修正案の提示のみ行い、修正自体はユーザー承認後に別途実施する |
| `/version-increment`        | Android・iOS のアプリバージョンを同じ値に更新してコミットする。`androidApp/build.gradle.kts` の `versionCode` / `versionName` と `project.pbxproj` の `CURRENT_PROJECT_VERSION` / `MARKETING_VERSION`（Debug・Release）を書き換える。引数なしならマイナー +1 案を提示。push・PR は行わない |

#### regression-reviewer のチェック項目

| チェック ID | 対象機能 | 主な確認内容 |
|------------|---------|------------|
| CHECK-1 | HistoryScreen 自動スクロール | `LaunchedEffect` のキーが `reportId` のみか／全件待機ループの有無／LazyColumn の item オフセット値／インデックス増分順序（increment-then-check）／`clearReportIdParam()` の呼び出しタイミング |
| CHECK-2 | RunStatus.Success 完了コールバックの LaunchedEffect ラップ | onCompleted 等のナビゲーション系コールバックが RunStatus.Success 分岐で LaunchedEffect(state) { onCompleted() } の形で呼ばれているか（LaunchedEffect なしで直接呼ぶと popBackStack() が二重実行されタブ誤遷移・空白画面につながる） |

> **チェック項目の追加方法**: `.claude/rules/regression-patterns.md` に `CHECK-N` セクションを追記する（`SKILL.md` 自体の変更は不要）。新たなデグレが発生した際は根本原因・検出方法・修正方針を記録し、次回以降の自動チェックに組み込む。

#### UI 操作系スキルの前提条件

`/android-ui-operator` と `/ios-ui-operator` は実機・シミュレータを直接操作して動作確認を行うスキルのため、他のスキルにはない追加のセットアップが必要です（Claude Code 自体はインストール済みの前提）。

**`/android-ui-operator`**
- Android SDK が導入済みで、Android CLI（`android` コマンド）が PATH に通っていること
- `adb`（Android SDK platform-tools に含まれる）
- 操作対象の Android エミュレータ（AVD）または実機が起動し、`adb devices` で認識されていること

`android layout` / `android screen` / `adb shell input` を Bash 経由で直接実行して UI を操作する。

**`/ios-ui-operator`**
- Xcode（Xcode Command Line Tools を含む）
- [Maestro CLI](https://maestro.dev/)（`brew tap mobile-dev-inc/tap && brew install mobile-dev-inc/tap/maestro`）
- Maestro MCP サーバーの登録（開発者ごとに1回、このMac上で実行）: `claude mcp add -s user maestro -- maestro mcp`
- 操作対象の iOS シミュレータが起動していること（`xcrun simctl boot "<device name>"`）

**UI 操作の仕組み**: iOS には Android CLI に相当する単発実行コマンドが無いため、[Maestro](https://maestro.dev/) の MCP サーバー（`maestro mcp`）を経由して操作する。Maestro は内部的に iOS 標準の XCUITest ベースの自前ドライバ（テスト実行中に常駐する HTTP サーバーを介して UI 階層取得・タップ命令をやり取りする方式）でシミュレータ・実機を制御しており、以前使われていた idb（Facebook製）は信頼性の問題により Maestro 自身によって置き換えられた経緯がある。MCP から提供される `list_devices` → `inspect_screen`（UI階層取得）→ `run`（その場で組み立てた1行の inline YAML を実行）というワークフローにより、Android 版と同様に「画面を見る → 判断する → 操作する → 再度画面を見る」という対話的な操作が可能。

制御パスを Android と比較した図: [docs/ios-ui-control-path.png](docs/ios-ui-control-path.png)

**セキュリティについて**: Maestro MCP はこの Mac 上で `maestro` CLI をローカル起動するだけであり、実際に使用する `list_devices` / `inspect_screen` / `take_screenshot` / `run` は外部と通信しない（Claude Code ↔ `maestro mcp` は標準入出力、`maestro mcp` ↔ シミュレータはローカルの XCUITest HTTP サーバー経由）。そのため、これらの操作でアプリの画面内容やデータが外部に漏れることはない。なお Maestro CLI 自体には匿名の利用状況分析（コマンド名・成否・実行時間などのメタデータのみで、アプリの実データやスクリーンショットは含まれない）がデフォルトで有効になっており、無効化したい場合は環境変数 `MAESTRO_CLI_NO_ANALYTICS` を設定する。

### サブエージェント（Agents）

`.claude/agents/` にカスタムサブエージェントを定義しています。

| エージェント          | 説明                                                                                                                                                                    |
|---------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `ui-ux-designer`    | Compose Multiplatform 画面の UI/UX レビュー・改善提案・実装を行う専門エージェント。Material Design 3 準拠・アクセシビリティ・ユーザビリティの観点で分析し、`.claude/agent-memory/ui-ux-designer/` に知識を蓄積する。`*Screen.kt`（`sharedUI/src/commonMain/kotlin/dev/seabat/ramennote/ui/screens/` 配下）を作成・大きく変更したとき、または UI/UX レビュー依頼時に自動的に起動を促す |

### Hooks

`.claude/settings.json` に以下の自動処理を設定しています（Claude Code 経由の操作にのみ働く）。

| タイミング                | 処理                                                                                          |
|--------------------------|-----------------------------------------------------------------------------------------------|
| `Edit` / `Write` 前      | `local.properties`・`google-services.json`・`.env` への変更をブロック |
| `Bash` 前（危険コマンド）| `push --force`・`reset --hard`・`clean -fd`・`rm -rf /` をブロック                           |
| `Edit` / `Write` 後      | 変更ファイルに応じてサブエージェント・スキル起動を促すリマインダを表示（`.claude/` 配下 または `build.gradle.kts` → readme-updater スキル／`*Screen.kt` → ui-ux-designer エージェント） |
| 応答完了時（Stop）       | macOS 通知で「応答が必要です」を表示                                                          |

これとは別に、`git commit` 実行時には git 標準の pre-commit フックが ktlint 整形と
`rules-reviewer`・`regression-reviewer` レビューを行います（Claude Code を介さない commit にも効く）。
詳細は「セットアップ」の [Git Hooks の設定](#git-hooks-の設定) を参照してください。

## ライセンス

このプロジェクトのライセンスについては [LICENSE](./LICENSE) ファイルを参照してください。

## 参考リンク

- [Kotlin Multiplatform 公式ドキュメント](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
