# @Preview / NavGraph アノテーション規約

- **`@Preview` import は必ず `androidx` 版を使用**: `androidx.compose.ui.tooling.preview.Preview`
  - `org.jetbrains.compose.ui.tooling.preview.Preview` は Compose Multiplatform 1.9.0 で deprecated。compose-nav-graph 0.2.0 の KSP プロセッサが `androidx` 版のみを検索するため統一必須
- **画面 Composable には `@NavDestination` と `@NavPreview` を付与**: IDE の NavGraph Graph ビューに表示するために必要
  - `@NavDestination`: ナビゲーショングラフのノードとして登録
  - `@NavPreview`: NavGraph Previews タブにサムネイルを表示

## 関連コマンド

```bash
./gradlew :sharedUI:generatePreviewGallery       # NavGraph Previews 生成（68件）
```
