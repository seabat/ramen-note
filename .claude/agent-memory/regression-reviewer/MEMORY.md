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

### [REGR-2] Note タブが登録完了時に別タブへ誤遷移（2026-09-23）
- **ファイル**: `AddAreaScreen.kt`・`AddShopScreen.kt`・`EditAreaSortScreen.kt` の
  `RunStatus.Success` 分岐
- **根本原因**: `onCompleted()`（呼び出し元は `noteRefreshKey++; navController.popBackStack()`）
  が `LaunchedEffect` で囲まれず、コンポーザブル本体で直接呼ばれていた。
  `addState` が `Success` のまま再コンポジションが起きると `popBackStack()` が二重実行され、
  `AddArea` → `Note` の2階層分 pop されて `Note` タブごと消え、`Home` タブに意図せず着地。
  着地先の状態が不整合で画面が空白になるケースもあった。
- **同時に発覚した既存の正しい実装**: `EditAreaScreen.kt`・`EditShopScreen.kt` は
  `LaunchedEffect(state) { onCompleted() }` で正しくラップ済みだった（対照パターンとして有用）。
- **修正**: 3ファイルとも `LaunchedEffect(state) { onCompleted() }` に統一
- **検出方法**: CHECK-2（agent 定義内のチェックリスト参照）
- **横展開の勘所**: `RunStatus<T>` を購読して `Success` 時にナビゲーション系コールバック
  （`onCompleted`・`onBack` 等）を呼ぶ画面はすべて同種のリスクを持つ。新規追加時は
  `EditAreaScreen.kt` の実装をテンプレートとして参照させるとよい。
