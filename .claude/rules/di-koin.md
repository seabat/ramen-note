# DI（Koin）登録ルール

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
