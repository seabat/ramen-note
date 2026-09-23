# 過去のデグレ再発防止パターン

ramen-note で実際に発生したデグレの原因と再発防止策を蓄積する。**このファイルは二重の役割を持つ**。

- **予防**: 該当箇所を新規実装・変更する前に読み、同じ原因を作り込まないようにする
- **検知**: `regression-reviewer` スキル（`.claude/skills/regression-reviewer/SKILL.md`）が
  commit 時にこのファイルを読み込み、ステージされた差分が各チェック項目に抵触していないか照合する

新たなデグレが発生した際は、本ファイルに `CHECK-N` セクションを追記して蓄積していく。
各セクションには「チェック対象ファイル」を明記すること（`regression-reviewer` スキルはこの記述を
読んで対象判定を行うため、このファイルへの追記だけで完結し、`SKILL.md` 側の変更は不要）。

---

## CHECK-1: HistoryScreen 自動スクロール機能

**チェック対象ファイル**:
`sharedUI/src/commonMain/kotlin/dev/seabat/ramennote/ui/screens/history/HistoryScreen.kt`

**背景**: HistoryScreen に UI 変更（LazyColumn への item 追加など）を行った際、
`reportId` 指定時の自動スクロール機能が2度にわたって壊れた。

**確認項目（すべて満たすこと）**:

### 1-A: LaunchedEffect のキーが `reportId` のみであること

```kotlin
// ✅ 正しい
LaunchedEffect(reportId) { ... }

// ❌ 誤り（reportsState が1件ずつ追加されるたびに再起動・キャンセルされ、
//          部分データで clearReportIdParam() が呼ばれ以後スクロール不能になる）
LaunchedEffect(reportId, reportsState) { ... }
```

**理由**: `HistoryViewModel.loadReports()` は Flow で1件ずつレポートを追加する。
`reportsState` をキーに含めると、各追加ごとに LaunchedEffect が再起動される。
部分データ時に `targetIndex == -1` → `clearReportIdParam()` → `reportId = null`
となり、全件読み込み後もスクロールが実行されなくなる。

### 1-B: 全件読み込み待機ロジックが存在すること

```kotlin
// ✅ 正しい（reportsState が安定するまで待つ）
var lastSize = -1
while (reportsState.size != lastSize) {
    lastSize = reportsState.size
    delay(100)
}
```

### 1-C: LazyColumn の item オフセットが正しいこと

`ReportList` 内の LazyColumn の item 構造と、スクロールインデックス計算のオフセットが一致していることを確認する。

**現在の LazyColumn 構造（`isSearchResultVisible == false` 時）**:
```
Index 0: item { Menu(...) }
Index 1: item { HintBanner(...) }
Index 2: item { Text(yearMonth) }  // グループ1ヘッダー
Index 3: report[0] of group1
Index 4: report[1] of group1
...
Index 2+n: Header of group2
Index 3+n: report[0] of group2
```

スクロールコード内の `currentIndex` 初期値が **2** であることを確認する：
```kotlin
// ✅ 正しい（Menu=0, HintBanner=1 の2アイテム分オフセット）
var currentIndex = 2

// ❌ 誤り（Menu/HintBanner が考慮されていない）
var currentIndex = 0
```

**LazyColumn に item が追加・削除された場合はオフセット値の見直しが必要**。
追加・削除された item の数だけ `currentIndex` の初期値を増減させること。

### 1-D: インデックスの増分順序が正しいこと

```kotlin
// ✅ 正しい（increment してから check）
for (report in monthReports) {
    currentIndex++ // レポート位置へ移動してから確認
    if (report.id == id) {
        targetIndex = currentIndex
        break@loop
    }
}
currentIndex++ // 次のグループのヘッダー位置へ移動

// ❌ 誤り（最初のレポートがヘッダーと同じインデックスになる）
for (report in monthReports) {
    if (report.id == reportId) {
        targetIndex = currentIndex
        break@loop
    }
    currentIndex++
}
```

### 1-E: `clearReportIdParam()` がスクロール**後**に呼ばれること

```kotlin
// ✅ 正しい（スクロール完了後にクリア）
if (targetIndex >= 0) {
    delay(...)
    listState.animateScrollToItem(targetIndex)
}
clearReportIdParam()

// ❌ 誤り（スクロール前にクリアするとreportIdがnullになりスクロール不能）
clearReportIdParam()
if (targetIndex >= 0) { ... }
```

### 1-F: `isSearchResultVisible == false` 時のみスクロールを試みること

**仕様上の確認済み事項**: `reportId` と `initialSearchText` は同時に指定されない。
したがって `reportId` が指定されている場合、`listIsSearchResultVisible` は常に `false` となる。
この仕様が変わらない限り、明示的なガード節がなくても **✅ PASS** とする。

もし将来 `reportId` と `initialSearchText` を同時に指定するケースが追加された場合は、
以下のガード節の追加を検討すること：

```kotlin
if (listIsSearchResultVisible) {
    clearReportIdParam()
    return@LaunchedEffect
}
```

**注意**: LazyColumn の構造（`item {}` / `items()` の数・順序）が変更された場合、必ず 1-C の
オフセット値を再計算すること。`isSearchResultVisible` の条件分岐が変わった場合も 1-F を
重点確認すること。

---

## CHECK-2: RunStatus.Success 完了コールバックの LaunchedEffect ラップ

**チェック対象ファイル**:
`onCompleted: () -> Unit` / `onBackClick` など、呼び出し元でナビゲーション（`popBackStack()` 等）を
行うコールバックを引数に持つ `*Screen.kt` 全般。特に `RunStatus<T>` を購読し、`Success` 時に
そのコールバックを呼ぶ画面（`AddAreaScreen.kt`、`AddShopScreen.kt`、`EditAreaScreen.kt`、
`EditAreaSortScreen.kt`、`EditShopScreen.kt` など）。

**背景**: `AddAreaScreen.kt` で、`RunStatus.Success` になった際に `onCompleted()`
（= `navController.popBackStack()`）を `LaunchedEffect` で囲まずコンポーザブル本体で直接呼んでいた。
副作用がリコンポジションのたびに再実行されうるため、`popBackStack()` が二重実行され、
`Note` タブごと pop されて `Home` タブへ意図せず遷移し、遷移後の画面が空白になるデグレが発生した
（2026-09-23、AddAreaScreen / AddShopScreen / EditAreaSortScreen の3箇所で同一パターンを確認）。

**確認項目（すべて満たすこと）**:

### 2-A: `RunStatus.Success` 分岐内でのコールバック呼び出しが `LaunchedEffect` で囲まれていること

```kotlin
// ✅ 正しい（state を key にした LaunchedEffect 内で呼ぶ）
is RunStatus.Success -> {
    LaunchedEffect(addStatus) {
        onCompleted()
    }
}

// ❌ 誤り（コンポーザブル本体で副作用を直接実行。リコンポジションのたびに
//          再実行され、ナビゲーションコールバックが二重・多重実行されうる）
is RunStatus.Success -> {
    onCompleted()
}
```

**理由**: Compose ではコンポーザブル本体は何度でも再実行されうる。`RunStatus.Success` が
StateFlow に保持されたまま何らかの理由で再コンポジションが走ると、`LaunchedEffect` なしでは
副作用（ここではナビゲーション呼び出し）が毎回再実行される。`popBackStack()` が意図せず
複数回呼ばれると、想定より多くのバックスタックエントリが pop され、タブ画面（`Note` 等）ごと
pop されて別タブに着地し、遷移先が不整合な状態（空白画面）になることがある。

### 2-B: `LaunchedEffect` のキーに該当する `RunStatus` の State 自体（または同等に変化する値）が使われていること

```kotlin
// ✅ 正しい
LaunchedEffect(addStatus) { onCompleted() }

// ⚠️ 要確認（Unit key だと画面初回表示時にしか発火しないため、
//           ボタン押下 → Success 遷移のタイミングによっては動作しないことがある）
LaunchedEffect(Unit) { onCompleted() }
```

**参考実装（正しいパターン）**: `EditAreaScreen.kt`・`EditShopScreen.kt` はこのパターンに
準拠済み。新規・変更時はこの2ファイルの実装を参照すること。

**注意**: `RunStatus<T>` を購読して `Success` 時に `onCompleted` 等のナビゲーション系コールバックを
呼ぶ画面を新規追加・変更した場合、必ず本チェックを実行すること（`*Screen.kt` 全般が対象）。
