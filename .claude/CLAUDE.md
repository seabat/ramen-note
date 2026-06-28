# ramen-note プロジェクト

## 概要
KMP（Kotlin Multiplatform）のラーメン店管理アプリ。Android / iOS 対応、Compose Multiplatform を使用。

## 言語・コミュニケーション
- コード内コメント・コミットメッセージ・PR・ドキュメントはすべて日本語で記述する

## 技術スタック
- **Kotlin**: 2.2.10 / **Compose Multiplatform**: 1.9.0
- **Room**: 2.8.1（DB） / **Koin**: 4.1.1（DI） / **Ktor**: 3.3.0（HTTP）
- **Coil**: 3.3.0（画像） / **Navigation Compose**: 2.9.0-rc01
- **SKIE**: 0.10.6（iOS 連携）
- **compose-nav-graph**: 0.2.0（NavGraph Graph ビュー / IDE プラグイン）
- **Firebase AI / Gemini AI**: 0.7.0（Android のみ）
- **Android SDK**: compileSdk 36, minSdk 24, targetSdk 36

## 実行コマンド
```bash
./gradlew :androidApp:assembleDebug              # ビルド
./gradlew ktlintCheck                            # lint チェック
./gradlew ktlintFormat                           # lint 自動修正
./gradlew :androidApp:testDebugUnitTest          # テスト
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

## コーディング規約

- **ViewModel**: Contract + 実装 + Mock の3点セット。状態は `MutableStateFlow`（private）+ `StateFlow`（public）。UseCase の Contract に依存する
- **UseCase**: Contract + 実装。単一責任（1操作）。`operator fun invoke()` で呼び出し。結果は `RunStatus<T>` でラップ
- **Repository**: Contract + 実装。Entity ↔ Domain モデル変換を担当。DataSource の Contract に依存する
- **DataSource**: プラットフォーム固有処理は `expect/actual` + Contract パターン
- **Database（Room）**: マイグレーション は `DataModule.common.kt` に記述。現在のバージョン: 5（AreaEntity, ShopEntity, ReportEntity）

### @Preview / NavGraph アノテーション規約

- **`@Preview` import は必ず `androidx` 版を使用**: `androidx.compose.ui.tooling.preview.Preview`
  - `org.jetbrains.compose.ui.tooling.preview.Preview` は Compose Multiplatform 1.9.0 で deprecated。compose-nav-graph 0.2.0 の KSP プロセッサが `androidx` 版のみを検索するため統一必須
- **画面 Composable には `@NavDestination` と `@NavPreview` を付与**: IDE の NavGraph Graph ビューに表示するために必要
  - `@NavDestination`: ナビゲーショングラフのノードとして登録
  - `@NavPreview`: NavGraph Previews タブにサムネイルを表示

## ファイル命名規則
- **expect/actual**: `Xxxx.common.kt` / `Xxxx.android.kt` / `Xxxx.ios.kt`
- **ViewModel**: `XxxxViewModelContract.kt` / `XxxxViewModel.kt` / `MockXxxxViewModel.kt`
- **UseCase**: `XxxxUseCaseContract.kt` / `XxxxUseCase.kt`
- **Repository**: `XxxxRepositoryContract.kt` / `XxxxRepository.kt`

## DI（Koin）登録ルール

エントリーポイント: `sharedUI` の `di/KoinHelper.kt` の `initKoin()`

| モジュール | ファイル | 登録方法 |
|---|---|---|
| `viewModelModule` | `sharedUI` `ui/di/ViewModelModule.kt` | `viewModel { XxxxViewModel(get(), ...) }` |
| `useCaseModule` | `sharedLogic` `domain/di/DomainModule.kt` | `single<Contract> { Impl(get(), ...) }` |
| `repositoryModule` | `sharedLogic` `data/di/DataModule.common.kt` | `single<Contract> { Impl(get(), ...) }` |
| `databaseModule` | `sharedLogic` `data/di/DataModule.common.kt` | `single<RamenNoteDatabase> { ... }` |
| `dataSourceModule` | `sharedLogic` `data/di/DataModule.common.kt` | expect/actual で定義 |
| `factoryModule` | `sharedLogic` `data/di/DataModule.common.kt` | expect/actual で定義 |
| `uiModule` | `sharedUI` `ui/di/UiModule.common.kt` | expect/actual で定義 |

## プラットフォーム固有 API
**いずれかを変更した場合、もう一方のプラットフォームにも同等の変更が必要。**

- **パターン1（expect/actual）**: androidMain / iosMain に Kotlin 実装。例: `logd`、`GalleryLauncher`、`DataModule`、`LifecycleObserver`
- **パターン2（Contract + Swift）**: commonMain で Contract 定義 → androidMain に Kotlin 実装 → `iosApp/` に Swift 実装。Swift 実装は `SwiftLibDependencyFactoryContract` 経由で Koin に登録。例: `ShopAiDataSource`、`UnsplashDataSource`

## 機密情報（API キー等）の管理
- `local.properties` に機密値を定義（`.gitignore` 済み）
- `sharedLogic/build.gradle.kts` の `generateBuildSecrets` タスクが `BuildSecrets.kt` を自動生成
- commonMain から `BuildSecrets.UNSPLASH_ACCESS_KEY` などで参照
- `local.properties`・`google-services.json`・`.env` への Claude Code からの編集は Hooks によりブロックされる

## ktlint 設定
無効化ルール（`.editorconfig`）: `trailing-comma`、`function-signature`、`parameter-list-wrapping`、`expression-body-syntax`、`backing-property` / `property-naming`（`_xxx` StateFlow 許可）、`filename`（`logd.android.kt` 形式許可）、Composable 関数名の除外

ktlint 対象外: テストコード、ビルド出力、Gradle ファイル、secrets ディレクトリ

## PR フォーマット
タイトル: 50文字以内、体言止め

本文:
- **概要**: 変更の目的と背景
- **変更内容**: 具体的な変更点をリスト形式で
- **テスト**: テスト方法と確認事項

## Hooks
自動動作の詳細は @.claude/settings.json を参照。

## 注意事項
- Room の KSP 生成タスクと Compose Resource 生成タスクに依存関係がある（build.gradle.kts 参照）
- iOS ビルドは Xcode から実行（`iosApp/` ディレクトリ）