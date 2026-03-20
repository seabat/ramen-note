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
- **SKIE**: Kotlin/Swift 連携（iOS）
- **Firebase**: Analytics・Crashlytics・AI（Android）
- **Gemini AI**: AI 機能（Android）

### 開発環境

- Kotlin: 2.2.10
- Compose Multiplatform: 1.9.0
- Android Gradle Plugin: 8.11.2
- Android SDK: minSdk 24, targetSdk 36, compileSdk 36

## プロジェクト構造

```
ramen-note/
├── composeApp/              # 共有コード
│   └── src/
│       ├── commonMain/      # 全プラットフォーム共通コード
│       ├── androidMain/     # Android 固有コード
│       └── iosMain/         # iOS 固有コード
├── iosApp/                  # iOS アプリケーション
└── gradle/                  # Gradle 設定
```

### 主要なパッケージ構成

- `data/`: データソース、リポジトリ、データベース
- `domain/`: ドメインモデル、ユースケース
- `ui/`: 画面、コンポーネント、ナビゲーション
- `di/`: 依存性注入の設定

## セットアップ

### 必要な環境

- JDK 11 以上
- Android Studio または IntelliJ IDEA
- Xcode (iOS ビルドの場合)

### Unsplash API の設定

エリアの画像を表示するために Unsplash API を使用しています。ビルド前に以下の手順で Access Key を設定してください。

1. [Unsplash Developers](https://unsplash.com/developers) でアプリケーションを登録し、Access Key を取得
2. プロジェクトルートの `local.properties` に以下を追加

```properties
UNSPLASH_ACCESS_KEY=取得した Access Key
```

ビルド時に Gradle が `local.properties` から値を読み込み、commonMain 向けに `BuildSecrets.kt` を自動生成します。アプリコードは `BuildSecrets.UNSPLASH_ACCESS_KEY` 経由で参照します。

**注意**: Access Key を設定せずにビルドすると、エリア画像の取得が正常に動作しません。

### ビルドと実行

#### Android アプリ

macOS/Linux:
```bash
./gradlew :composeApp:assembleDebug
```

Windows:
```bash
.\gradlew.bat :composeApp:assembleDebug
```

#### iOS アプリ

1. Xcode で `/iosApp` ディレクトリを開く
2. Xcode から実行するか、IDE の実行設定を使用

### コードスタイル (ktlint)

- チェック:  
  ```bash
  ./gradlew ktlintCheck
  ```
- フォーマット:  
  ```bash
  ./gradlew ktlintFormat
  ```

## Claude Code

このプロジェクトは [Claude Code](https://claude.ai/code) による AI 支援開発に対応しています。

### プロジェクト設定（CLAUDE.md）

`.claude/CLAUDE.md` にアーキテクチャ・コーディング規約・DI 登録ルール・PR フォーマットなどのプロジェクト固有の指示を記載しています。Claude Code はこのファイルを自動的に読み込み、プロジェクトのルールに従った提案・実装を行います。

### スキル（Skills）

Claude Code のカスタムスキルを `.claude/skills/` に定義しています。

| スキル          | 説明                                                                                                                                         |
|----------------|----------------------------------------------------------------------------------------------------------------------------------------------|
| `/pr-create`   | 現在のブランチから PR を日本語で作成。ベースブランチを自動検出してユーザーに確認後、所定のフォーマットで `gh pr create` を実行する            |
| `/release-prep`| リリース前の準備作業。現在ブランチと main のバージョン比較・確認 → 前回リリース差分の把握 → ストア向けリリースノートの作成・保存             |

### サブエージェント（Agents）

`.claude/agents/` にカスタムサブエージェントを定義しています。

| エージェント          | 説明                                                                                                                                                                    |
|---------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `ui-ux-designer`    | Compose Multiplatform 画面の UI/UX レビュー・改善提案・実装を行う専門エージェント。Material Design 3 準拠・アクセシビリティ・ユーザビリティの観点で分析し、`.claude/agent-memory/ui-ux-designer/` に知識を蓄積する |
| `regression-reviewer` | 過去に発生したデグレの再発防止チェックリストに基づきコード変更を静的レビューするエージェント。`HistoryScreen.kt` や LazyColumn 構造を変更した際に自動的に起動を促す。確認済みデグレパターンは `.claude/agent-memory/regression-reviewer/` に蓄積する |
| `readme-updater`      | `README.md` をプロジェクトの実態と常に同期させるエージェント。スキル・エージェント・Hooks・技術スタックの変更時に該当セクションを更新する。更新パターンは `.claude/agent-memory/readme-updater/` に蓄積する |

#### regression-reviewer のチェック項目

| チェック ID | 対象機能 | 主な確認内容 |
|------------|---------|------------|
| CHECK-1 | HistoryScreen 自動スクロール | `LaunchedEffect` のキーが `reportId` のみか／全件待機ループの有無／LazyColumn の item オフセット値／インデックス増分順序（increment-then-check）／`clearReportIdParam()` の呼び出しタイミング |

> **チェック項目の追加方法**: `.claude/agents/regression-reviewer.md` に `CHECK-N` セクションを追記する。新たなデグレが発生した際は根本原因・検出方法・修正方針を記録し、次回以降の自動チェックに組み込む。

### Hooks

`.claude/settings.json` に以下の自動処理を設定しています。

| タイミング                | 処理                                                                                          |
|--------------------------|-----------------------------------------------------------------------------------------------|
| `Edit` / `Write` 前      | `composeApp/secrets/`・`local.properties`・`google-services.json`・`.env` への変更をブロック |
| `Bash` 前（危険コマンド）| `push --force`・`reset --hard`・`clean -fd`・`rm -rf /` をブロック                           |
| `Bash` 前（コミット）    | `git commit` 前に `ktlintFormat` を自動実行し、フォーマット済みファイルをステージング        |
| 応答完了時（Stop）       | macOS 通知で「応答が必要です」を表示                                                          |

## ライセンス

このプロジェクトのライセンスについては [LICENSE](./LICENSE) ファイルを参照してください。

## 参考リンク

- [Kotlin Multiplatform 公式ドキュメント](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
