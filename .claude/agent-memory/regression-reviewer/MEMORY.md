# Regression Reviewer Memory

## 確認済みデグレパターン

### [REGR-1] HistoryScreen 自動スクロール（2026-03-20）
- **ファイル**: `HistoryScreen.kt` の `LaunchedEffect` ブロック
- **根本原因A**: `HistoryViewModel` がレポートを Flow で1件ずつ追加するため、
  `LaunchedEffect(reportId, reportsState)` が毎回再起動される。
  部分データで `targetIndex=-1` → `clearReportIdParam()` → `reportId=null` → 以後スクロール不能
- **根本原因B**: `Menu` / `HintBanner` の2アイテムが LazyColumn の先頭に追加されたが、
  インデックス計算のオフセットが更新されなかった（0 のまま）
- **根本原因C**: inner loop が check-then-increment 順だったため、
  各グループの先頭レポートがヘッダーと同じインデックスになった
- **修正**: キーを `reportId` のみに変更 + 全件待機ループ + オフセット 2 + increment-then-check
- **検出方法**: CHECK-1（agent 定義内のチェックリスト参照）
