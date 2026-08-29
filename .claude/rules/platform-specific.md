# プラットフォーム固有 API

**いずれかを変更した場合、もう一方のプラットフォームにも同等の変更が必要。**

- **パターン1（expect/actual）**: androidMain / iosMain に Kotlin 実装。例: `logd`、`GalleryLauncher`、`DataModule`、`LifecycleObserver`
- **パターン2（Contract + Swift）**: commonMain で Contract 定義 → androidMain に Kotlin 実装 → `iosApp/` に Swift 実装。Swift 実装は `SwiftLibDependencyFactoryContract` 経由で Koin に登録。例: `ShopAiDataSource`、`UnsplashDataSource`
