---
name: android-device-interactor
description: Android 実機 / エミュレータを操作して動作確認を行う。UI レイアウトの取得、要素座標の特定、タップ・テキスト入力・スワイプ・キーイベント送出を Android CLI と adb を使って実行する。
---

# ツール
`android layout --help` および `android screen --help` を実行して詳細を確認してください。

## UI ダンプ
`android layout` は、画面上の UI 要素をフラットな JSON リストで返します。
`android layout --diff` は、前回の `layout` または `layout --diff` 呼び出し以降に変化した UI 要素のみをフラットな JSON リストで返します。

各 JSON オブジェクトは Android アプリの UI 要素を表します。次のプロパティが含まれます：
- `text` - 要素が持つテキスト
- `resource-id` - 要素を参照するための Android リソース ID
- `content-desc` - アクセシビリティツール向けの UI 要素の説明
- `interactions` - 要素がサポートするユーザー操作の種類。`checkable`、`clickable`、`focusable`、`scrollable`、`long-clickable`、`password` のいずれか、または複数
- `state` - 要素の現在の状態。`checked`、`focused`、`selected` のいずれか、または複数
- `bounds` - 要素のバウンディングボックスの画面座標。`[最小X,最小Y][最大X,最大Y]` 形式
- `center` - 要素の中心の画面座標。`[x,y]` 形式
- `off-screen` - true の場合、要素は UI 階層に存在するが表示されていない。スクロールすれば見える可能性がある

Android アプリの動作確認の主な手段として `layout` を使用してください。変化点の把握やコンテキストを小さく保つには `layout --diff` を使用してください。
例：電卓に数字を入力するとき、`layout --diff` を使うと数字表示の要素のみ出力されます。

WebView やアニメーションが表示されている場合、`layout` が失敗することがあります。その場合は `android screen --annotate` を使用してください。
現在の画面から別の画面へ移動したら、この問題は解消される可能性があります。

## スクリーンショット
`android screen capture -o <ファイルパス>` は、現在のデバイス画面の PNG を `<ファイルパス>` に保存します。

Android アプリの動作確認の補助的な手段として `screen capture` を使用してください。
使用例：
- 画面上の画像の内容を理解したい場合
- `WebView` を確認したい場合（Web コンテンツは UI ダンプに表示されないことがあります）
- UI 要素を見た目から探したい場合

**重要**：`android screen` から返された PNG 画像は、他の操作をする前に必ず*目視で*確認してください。

## アノテーション付きスクリーンショット
`android screen capture --annotate -o <ファイルパス>`
`android screen resolve --screen <パス> --string <文字列>`

`--annotate` コマンドは UI 要素の周囲に数字ラベルとバウンディングボックスを追加します。`layout` の出力から特定できない UI 要素を見つけるために使用してください。

**重要**：`android screen --annotate` を使用した場合は、生成された PNG ファイルを必ず*目視で*確認してください。

入力コマンドでこれらのラベルを参照するには、`screen resolve` を使ってラベルを座標に変換してください：

`android screen resolve --screen <ファイルパス> --string "#3"` は `<region 3 の x 座標> <region 3 の y 座標>` を返します。

ターン数を節約するため、シェルコマンドを組み合わせて使えます：

`adb shell input $(android screen resolve --screen screen.png --string "tap #34")`

このコマンドは `screen.png` の #34 の領域をタップします。

## 入力操作
Android デバイスとのインタラクションには `adb shell input` を使用してください。
特定の要素に対してどの操作が実行できるかは、要素の `"interactions"` プロパティを参照してください。

UI 要素は `center` 座標または `bounds` 座標を使って操作してください：

{
"key": -248568265,
"class": "android.widget.Button",
"bounds": "[138,9][167,38]",
"center": "[152,23]"
}

このボタンをタップするには `adb shell input tap 152 23` を実行します。中心座標をタップします。

{
"key": 12487234,
"class": "com.example.ui.ScrollableList",
"bounds": "[100,200][400,600]",
"center": "[250,400]"
}

このリストを下にスクロールするには `adb shell input swipe 250 400 600 500` を実行します。中心から下方向へ 500ms かけてスワイプします。

# Android 操作ルール
1. テキスト入力フィールドに文字を入力する前に、必ず `"state"` リストに `"focused"` が含まれていることを確認してください
2. 要素の `"interactions"` リストに `"scrollable"` が含まれている場合、見つからない UI 要素を探すためにスクロールを試みてください
3. スクロール操作は常にゆっくり実行してください。`adb shell input swipe` の第5引数でスクロール時間を制御できます
4. コンテンツの読み込みに時間がかかる場合があります。操作後に `layout` で情報が不足している場合は、数秒待ってから `layout --diff` を実行して変化を確認してください
