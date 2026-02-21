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
- **Firebase AI / Gemini AI**: 0.7.0（Android のみ）
- **Android SDK**: compileSdk 36, minSdk 24, targetSdk 36

## 実行コマンド
```bash
# ビルド
./gradlew :composeApp:assembleDebug

# lint
./gradlew ktlintCheck
./gradlew ktlintFormat

# テスト
./gradlew :composeApp:testDebugUnitTest
```

## アーキテクチャ
クリーンアーキテクチャ + MVVM + Android 推奨アーキテクチャの3層構造。

```
UI (Screen / ViewModel) → Domain (UseCase) → Data (Repository / DataSource / Database)
```

- 各層は **Contract（インターフェース）** を介して依存する
- 上位層は下位層の Contract のみに依存し、実装を直接参照しない

## プラットフォーム固有 API

KMP では共通コード（commonMain）からプラットフォーム固有の API を利用する方法が2つある。
**いずれかを変更した場合、もう一方のプラットフォームにも同等の変更が必要。**

### パターン1: expect/actual（androidMain / iosMain）

`expect` で共通インターフェースを宣言し、`actual` で各プラットフォームの実装を提供する。

```kotlin
// commonMain — expect 宣言（logd.common.kt）
expect fun logd(tag: String = "[RamenNote]", message: String)

// androidMain — actual 実装（logd.android.kt）
actual fun logd(tag: String, message: String) {
    if (BuildConfig.DEBUG) { Log.d(tag, message) }
}

// iosMain — actual 実装（logd.ios.kt）
actual fun logd(tag: String, message: String) {
    if (Platform.isDebugBinary) { println("$tag: $message") }
}
```

本プロジェクトの例: `logd`、`GalleryLauncher`、`DataModule`、`LifecycleObserver` など

### パターン2: Contract + Swift 実装（androidMain / iosApp Swift）

commonMain で Contract（インターフェース）を定義し、Android 側は androidMain で Kotlin 実装、iOS 側は `iosApp/` 配下で Swift 実装を提供する。iOS の Swift 実装は `SwiftLibDependencyFactoryContract` 経由で Koin に登録される。

```
commonMain:    ShopAiDataSourceContract（インターフェース）
androidMain:   ShopAiDataSource.kt（Kotlin 実装）
iosApp/Swift:  IosShopAiDataSource.swift（Swift 実装）
```

本プロジェクトの例: `ShopAiDataSource`、`UnsplashDataSource` などの DataSource

## ディレクトリ構造
```
composeApp/src/
├── commonMain/kotlin/dev/seabat/ramennote/
│   ├── config/          # アプリ設定
│   ├── data/
│   │   ├── database/    # Room Entity, DAO, Database
│   │   ├── datasource/  # DataSource Contract + 実装
│   │   ├── di/          # Data層 Koinモジュール
│   │   └── repository/  # Repository Contract + 実装
│   ├── di/              # KoinHelper（エントリーポイント）
│   ├── domain/
│   │   ├── di/          # Domain層 Koinモジュール
│   │   ├── extension/   # 拡張関数
│   │   ├── model/       # ドメインモデル
│   │   ├── usecase/     # UseCase Contract + 実装
│   │   └── util/        # RunStatus など
│   └── ui/
│       ├── components/  # 共通UIコンポーネント
│       ├── di/          # UI層 Koinモジュール
│       ├── navigation/  # ナビゲーション定義
│       ├── screens/     # 画面別フォルダ（Screen + ViewModel）
│       └── theme/       # テーマ定義
├── androidMain/         # Android固有実装（actual）
└── iosMain/             # iOS固有実装（actual）
```

## コーディング規約

### ViewModel 層
- **Contract（インターフェース）** + **実装** + **Mock** の3点セットで構成
- 状態は `MutableStateFlow`（private）+ `StateFlow`（public）で公開
- 処理は `viewModelScope.launch` 内で実行
- UseCase の Contract に依存する

```kotlin
// Contract
interface ShopViewModelContract {
    val shop: StateFlow<Shop?>
    fun loadShopAndImage(id: Int)
}

// 実装
class ShopViewModel(
    private val loadShopUseCase: LoadShopUseCaseContract
) : ViewModel(), ShopViewModelContract {
    private val _shop = MutableStateFlow<Shop?>(null)
    override val shop: StateFlow<Shop?> = _shop.asStateFlow()

    override fun loadShopAndImage(id: Int) {
        viewModelScope.launch {
            _shop.value = loadShopUseCase.invoke(id)
        }
    }
}
```

### UseCase 層
- **Contract + 実装** パターン。単一責任（1 UseCase = 1操作）
- `operator fun invoke()` で呼び出し可能にする
- 結果を返す場合は `RunStatus<T>` でラップする

```kotlin
// RunStatus（結果ラッパー）
sealed class RunStatus<T>(val data: T? = null, val message: String? = null) {
    class Idle<T> : RunStatus<T>()
    class Success<T>(data: T) : RunStatus<T>(data = data)
    class Error<T>(errorMessage: String) : RunStatus<T>(message = errorMessage)
    class Loading<T> : RunStatus<T>()
}

// Contract
interface LoadImageUseCaseContract {
    suspend operator fun invoke(name: String): RunStatus<ByteArray?>
}

// 実装
class LoadImageUseCase(
    private val localImageRepository: LocalImageRepositoryContract
) : LoadImageUseCaseContract {
    override suspend operator fun invoke(name: String): RunStatus<ByteArray?> =
        RunStatus.Success(localImageRepository.load(name))
}
```

### Repository 層
- **Contract + 実装** パターン
- Entity ↔ Domain モデルの変換を担当
- DataSource の Contract に依存する

### DataSource 層
- プラットフォーム固有の処理は `expect/actual` + Contract パターン
- 命名規則: `Xxxx.common.kt`（expect）/ `Xxxx.android.kt` / `Xxxx.ios.kt`（actual）

### Database 層（Room）
- Entity: `@Entity(tableName = "テーブル名")` + `data class XxxxEntity`
- DAO: `@Dao interface XxxxDao` に Query/Insert/Update/Delete を定義
- マイグレーション: `Migration(fromVersion, toVersion)` を `DataModule.common.kt` に記述
- 現在のバージョン: 5（entities: AreaEntity, ShopEntity, ReportEntity）

## DI（Koin）登録ルール

エントリーポイント: `di/KoinHelper.kt` の `initKoin()`

| モジュール | ファイル | 登録方法 |
|---|---|---|
| `viewModelModule` | `ui/di/ViewModelModule.kt` | `viewModel { XxxxViewModel(get(), ...) }` |
| `useCaseModule` | `domain/di/DomainModule.kt` | `single<Contract> { Impl(get(), ...) }` |
| `repositoryModule` | `data/di/DataModule.common.kt` | `single<Contract> { Impl(get(), ...) }` |
| `databaseModule` | `data/di/DataModule.common.kt` | `single<RamenNoteDatabase> { ... }` |
| `dataSourceModule` | `data/di/DataModule.common.kt` | expect/actual で定義 |
| `factoryModule` | `data/di/DataModule.common.kt` | expect/actual で定義 |
| `uiModule` | `ui/di/UiModule.common.kt` | expect/actual で定義 |

## ファイル命名規則
- **expect/actual**: `Xxxx.common.kt` / `Xxxx.android.kt` / `Xxxx.ios.kt`
- **ViewModel**: `XxxxViewModelContract.kt` / `XxxxViewModel.kt` / `MockXxxxViewModel.kt`
- **UseCase**: `XxxxUseCaseContract.kt` / `XxxxUseCase.kt`
- **Repository**: `XxxxRepositoryContract.kt` / `XxxxRepository.kt`

## ktlint 設定（.editorconfig）
以下のルールが無効化されている:
- `trailing-comma`: 末尾カンマ不要
- `function-signature`: 関数シグネチャの改行制御なし
- `parameter-list-wrapping`: パラメータリストの改行制御なし
- `expression-body-syntax`: 式本体構文の強制なし
- `backing-property` / `property-naming`: StateFlow の `_xxx` パターンを許可
- `filename`: `logd.android.kt` のような命名を許可
- `function_naming_ignore_when_annotated_with = Composable`: Composable関数名を除外

**ktlint 対象外**: テストコード、ビルド出力、Gradle ファイル、secrets ディレクトリ

## PR フォーマット
```
## タイトル
簡潔な変更内容の要約

## 本文
### 概要
- 変更の目的と背景

### 変更内容
- 具体的な変更点をリスト形式で

### テスト
- テスト方法と確認事項
```

## Hooks 設定（.claude/settings.json）

settings.json 内の hooks 配列は上から順に以下の役割を持つ:

### 1. 機密ファイル保護（PreToolUse: Edit|Write）
- **目的**: API キーや認証情報を含むファイルの誤編集を防止
- **対象パス**: `composeApp/secrets/`、`local.properties`、`google-services.json`、`.env`
- **動作**: 対象ファイルへの Edit/Write を検出すると **exit 2 でブロック**（ツール実行を拒否）

### 2. 危険コマンドブロック（PreToolUse: Bash）― 1つ目の Bash matcher
- **目的**: 取り返しのつかない Git 操作を防止
- **検出パターン**: `push --force`、`push -f`、`reset --hard`、`clean -fd`、`rm -rf /`
- **動作**: 危険なコマンドを検出すると **exit 2 でブロック**

### 3. ktlint 自動整形（PreToolUse: Bash）― 2つ目の Bash matcher
- **目的**: コミット前に Kotlin コードを自動フォーマットし、スタイル違反の混入を防止
- **トリガー**: `git commit` を含むコマンドを検出したとき
- **処理フロー**:
  1. `./gradlew ktlintFormat --rerun-tasks` でプロジェクト全体をフォーマット
  2. `git add -u` で修正されたファイルを再ステージング
  3. その後 Claude Code が git commit を実行（修正済みコードがコミットされる）
- **備考**: `--rerun-tasks` は Gradle キャッシュによるスキップを防止するために必要

### 4. macOS 通知（Stop）
- **目的**: Claude Code の応答完了をユーザーに通知
- **動作**: `osascript` でネイティブ通知（タイトル「Claude Code」+ Glass 効果音）を表示

## 注意事項
- `composeApp/secrets/` 配下に API キー（UnsplashConfig 等）があるため、**絶対にコミットしない**
- Room の KSP 生成タスクと Compose Resource 生成タスクに依存関係がある（build.gradle.kts 参照）
- iOS ビルドは Xcode から実行（`iosApp/` ディレクトリ）
