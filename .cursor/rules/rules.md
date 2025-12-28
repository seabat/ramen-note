# Pull Request の形式

Pull Requestのタイトルと変更内容を提案する際は、以下の形式に従ってください：

## PRタイトル
- 機能や変更の内容を簡潔に表現
- 例: 「Gemini AI を用いた店舗情報自動入力機能の追加」

## PR本文の構成

### 概要
- 変更の目的と概要を1-2文で説明

### 主な変更点
- 機能や変更をカテゴリごとに分類（1-5個程度）
- 各カテゴリには見出しと箇条書きで説明を記載
- 技術的な詳細は不要（使用技術、アーキテクチャの詳細など）

### 影響範囲
- 新規ファイル: 追加された主要なファイルを列挙
- 変更ファイル: 変更された主要なファイルを列挙
- 破壊的変更: 有無を明記（通常は「なし」）

## PR本文の例（Markdown形式）

```markdown
### 概要
食レポ画面で画像をタップして拡大表示できる機能を追加しました。また、ダイアログコンポーネントを共通化してコードを整理しました。

### 主な変更点

**1. 食レポ画像表示機能の追加**
- 画像をタップすると拡大表示されるダイアログを追加
- 食レポ一覧画面で画像をタップすると拡大表示されるように実装

**2. ダイアログコンポーネントの共通化**
- 共通のダイアログコンポーネントを追加
- 既存のダイアログを共通コンポーネントを使用するようにリファクタリング

**3. コンポーネントの整理**
- コンポーネントを機能別にディレクトリに整理
- 既存のコンポーネントを新しいディレクトリ構造に移動

### 影響範囲
- 新規ファイル: `WideDialog`, `ReportImageDialog`
- 変更ファイル: `ReportCard`, `HistoryScreen`, `ScheduleMenuDialog`, `FavoriteShopMenuDialog`, その他コンポーネント使用箇所
- 破壊的変更: なし
```

## 注意事項
- コミットごとの変更ではなく、ブランチ全体での変更内容をまとめる
- 技術的な詳細（使用技術、アーキテクチャの詳細など）は記載しない
- 簡潔で分かりやすい説明を心がける

# コーディングルール

## アーキテクチャ

* 基本的にクリーンアーキテクチャー、MVVM アーキテクチャー、Android 推奨アーキテクチャーのルールに従う。

* 端末のストレージへのデータ保存・書き込みは ViewModel 層 -> UseCase 層 -> Repository 層 -> DataSource 層を使用する

* 端末のデータベースへのデータ保存・書き込みは ViewModel 層 -> UseCase 層 -> Repository 層 -> DataBase 層を使用する

* Web API 使用等通信を伴う処理は ViewModel 層 -> UseCase 層 -> Repository 層 を使用する

## ViewModel 層

MVVM アーキテクチャの VM に相当する。

* Screen クラスと同じディレクトリに配置する。
* `XxxxViewModelContract`、`XxxxViewModel`、`MockXxxxViewModel` で構成する。
* `XxxxViewModelContract` はインタフェース。
* `XxxxViewModel` は `XxxxViewModelContract` の実装クラス。
* `MockXxxxViewModel` は `XxxxViewModelContract` の実装クラスでプレビューで使用するためのモックである。
* ViewModel は Koin を使って `UseCaseContract` または `RepositoryContract` をコンストラクタに inject する。ただし、`RepositoryContract` の inject はできるだけ避ける。
* ViewModel を新規に作成する際は `viewModelModule` に `viewModel` を使って Module 登録する。

## UseCase 層

MVVM アーキテクチャの M に相当する。クリーンアーキテクチャのユースケースに相当する。

* UseCase の戻り値は `RunStatus<T>` を使用する。
* Domain 層は他の層（UI、Data）に依存しない。
* `domain/usecase/` に配置する。
* `XxxxUseCase` と `XxxxUseCaseContract` で構成する。
* `XxxxUseCaseContract` はインタフェース。
* `XxxxUseCase` は `XxxxUseCaseContract` の実装クラス。
* UseCase は Koin を使って `RepositoryContract` をコンストラクタに inject する。
* UseCase を新規作成する際は `useCaseModule` に `single` を使って Koin Module 登録する。

## Repository 層

UseCase 層から呼び出され、各種データにアクセスする玄関口の役割となる。

* `data/repository/` に配置する。
* `XxxxRepository` と `XxxxRepositoryContract` で構成する。
* `XxxxRepositoryContract` はインタフェース。
* `XxxxRepository` は `XxxxRepositoryContract` の実装クラス。
* Repository は必要に応じて Koin を使って `DataSourceContract` または Database をコンストラクタに inject する。
* Repository を新規作成する際は `repositoryModule` に `single` を使って Koin Module 登録する。

## DataSource 層

Android / iOS それぞれの端末のストレージにアクセスする処理を提供する。

* `data/datasource/` に配置する。
* インタフェースとなる `DataSourceContract` クラスを `commonMain` に配置する。`XxxxDataSourceContract` を実装し、Android 端末にアクセスする `AndroidXxxxDataSource` クラスを `androidMain` に配置する。`DataSourceContract` を実装し、iOS 端末のストレージにアクセスする `IosXxxxDataSource` クラスを Xcode プロジェクトの `iosApp/Kmp/` に配置する。
* DataSource を新規登録する際は `dataSourceModule` に `single` を使って Koin Module 登録する。`dataSourceModule` は `expect/actual` である。

## DataBase 層

Kotlin Multiplatform 対応の Room を使ってデータベースへアクセスする処理を提供する。

* `data/database/` に配置する。
* DAO は `data/database/dao/` に配置する。
* Entity は `data/database/entity/` に配置する。
* RoomDatabase 継承クラスは `RamenNoteDatabase`。
* `DatabaseFactoryContract` は `RoomDatabase.Builder<RamenNoteDatabase>` を取得するためのインタフェース。
* Android 側の `DatabaseFactoryContract` 実装クラスは `androidMain` に `AndroidDatabaseFactory` として配置。
* iOS 側の `DatabaseFactoryContract` 実装クラスは `iosMain` に `IosDatabaseFactory` として配置。
* `RamenNoteDatabase` は `databaseModule` に `single` を使ってモジュール登録する。`databaseModule` は `expect/actual` である。
* `DatabaseFactoryContract` は `factoryModule` に `single` を使って Koin Module 登録する。`factoryModule` は `expect/actual` である。

## UI 層（Compose）

Jetpack Compose を使用した UI コンポーネントの層。

* Screen クラスは `ui/screens/` に機能別に配置する。
* Screen クラスは `XxxxScreen` という命名規則に従う。
* Dialog コンポーネントは `ui/components/dialog/` に配置する。ただし、特定の Screen 専用の Dialog は、その Screen と同じディレクトリに配置する。
* Alert コンポーネントは `ui/components/alert/` に配置する。
* Button コンポーネントは `ui/components/button/` に配置する。
* 共通で使用される UI コンポーネントは `ui/components/` に配置する。
* プラットフォーム固有の UI コンポーネントは `expect/actual` を使用する。`expect` は `commonMain` に、`actual` は `androidMain` または `iosMain` に配置する。

## Domain Model 層

* ドメインモデルを定義する層。
* Domain Model は `domain/model/` に配置する。
* Domain Model は `data class` または `sealed class` を使用する。
* エラーハンドリングには `RunStatus<T>` を使用する。`RunStatus` は `Idle`、`Loading`、`Success`、`Error` の4つの状態を持つ。

## エラーハンドリング

* 非同期処理の結果は `RunStatus<T>` で表現する。
* ViewModel は `RunStatus` を `StateFlow` で管理し、UI に公開する。
* UI では `RunStatus` の各状態に応じて適切な表示を行う（Loading 時はプログレスバー、Error 時はエラーダイアログなど）。

## プラットフォーム固有コード

* プラットフォーム固有の実装が必要な場合は `expect/actual` を使用する。
* `expect` は `commonMain` に配置する。
* `actual` は `androidMain` または `iosMain` に配置する。
* DataSource の実装は `expect/actual` ではなく、Contract パターンを使用する（DataSource 層のルールを参照）。

## DI（依存性注入）Module

* Koin Module は各層の `di/` ディレクトリに配置する。
* `viewModelModule` は `ui/di/` に配置する。
* `useCaseModule` は `domain/di/` に配置する。
* `repositoryModule`、`dataSourceModule`、`databaseModule`、`factoryModule` は `data/di/` に配置する。
* `uiModule` は `ui/di/` に配置し、`expect/actual` で実装する。

## ファイル名

* Kotlin Multiplatform の expect/actual 実装
    * `expect` 宣言を含むファイルは `commonMain` に配置し、`Xxxx.common.kt` とする
    * `actual` 実装を含むファイルで `androidMain` に配置するものは `Xxxx.android.kt` とする
    * `actual` 実装を含むファイルで `iosMain` に配置するものは `Xxxx.ios.kt` とする
    * 同じ機能の expect/actual は同じベース名（`Xxxx`）を使用する

