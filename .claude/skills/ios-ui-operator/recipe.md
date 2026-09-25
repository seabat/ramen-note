# ramen-note iOS UI 操作レシピ

`SKILL.md` の Maestro MCP 操作基準を前提に、ramen-note アプリ固有のビルド手順・画面遷移・機能別の動作確認手順をまとめる。

新しい機能を追加したり既存機能を改修したりした場合は、実際に本レシピの手順で動作確認したうえで、変更点をこのファイルに反映すること。

`android-ui-operator/recipe.md`（Android版）と対になる内容。画面構成・ナビゲーションは基本的に共通（sharedUI の Compose Multiplatform コードをそのまま共有しているため）だが、**プラットフォーム固有の差異と Maestro 特有の操作クセ**がいくつかある。これらは必ず押さえること。

## デバイス確定・起動手順

`SKILL.md`「デバイス選択」の手順（起動中デバイスを先に確認する）に従う。このプロジェクトでの推奨デフォルトは **`iPhone 17`**（`.claude/rules/dependencies.md` の SPM トラブルシュート例でも使われている機種）。

```bash
xcrun simctl list devices booted                # 手順1: 起動中の iOS シミュレータを確認
# 0台なら以下で推奨デフォルトを起動、1台ならそれを使う、複数ならユーザーに確認する
xcrun simctl boot "iPhone 17"                   # 手順4: 対象デバイスを確定してから起動（起動済みならエラーは無視してよい）

# 対象デバイスを確定・起動した後で Simulator.app を開く（順序を守る。詳細は SKILL.md 参照）
open -a Simulator
osascript -e 'tell application "Simulator" to activate' \
  -e 'tell application "System Events" to tell process "Simulator" to perform action "AXRaise" of (first window whose name contains "iPhone 17")'

xcrun simctl list devices booted                # 起動中デバイスの UDID を確認
```

`device_id`（UDID）が決まったら、以降の `inspect_screen` / `take_screenshot` / `run`（MCP）や `maestro --device <UDID> ...`（CLI）はすべてこの ID を使う。

## ビルド・起動手順

```bash
cd /path/to/ramen-note
xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp -configuration Debug \
  -destination 'platform=iOS Simulator,name=iPhone 17' build

# ビルド成果物のパスは DerivedData 配下。毎回変わるので都度 find で確認する
APP_PATH=$(find ~/Library/Developer/Xcode/DerivedData/iosApp-*/Build/Products/Debug-iphonesimulator -maxdepth 1 -name "*.app")
xcrun simctl install "iPhone 17" "$APP_PATH"
xcrun simctl launch "iPhone 17" dev.seabat.ramennote
```

- バンドルID（appId）: `dev.seabat.ramennote`
- 既存のアプリデータは `simctl install` では消えない（Android の `installDebug` と同様）
- SPM（Swift Package Manager）の解決に失敗する場合は `.claude/rules/dependencies.md` の「iOS の Swift Package（SPM）が壊れたときの対処」を参照

## 画面遷移マップ

ボトムナビゲーション（5タブ、ルート画面のみに表示）: `ホーム` / `予定` / `ノート` / `食レポ` / `設定`。画面構成は Android 版と共通。

```
ホーム (HomeScreen)
├─ 訪店予定・年間食レポ回数グラフ・最近の食レポ・お気に入り店（一覧表示のみ）

ノート (NoteScreen) … エリア一覧
├─ [+] → エリア登録 (AddAreaScreen)
├─ カード長押し → エリア編集 (EditAreaScreen) → 削除する
├─ 並び替え → エリアの並び替え (EditAreaSortScreen)
├─ エリアカード本体タップ（「地図」「一覧」ボタン以外の部分）→ エリア店舗一覧 (AreaShopListScreen)
│   ├─ [+] → 店舗登録 (AddShopScreen, AI自動入力あり)
│   └─ 店舗タップ → 店舗詳細 (ShopScreen)
│       ├─ 📋追加/変更 → 訪問予定日をDatePickerで選択（即時登録、専用フォームなし）
│       ├─ 🍜追加 → 食レポ登録 (AddReportScreen)
│       ├─ 🍜一覧 → 食レポ一覧 (HistoryScreen、店舗フィルタ付き)
│       │           └─ カード長押し → 食レポ編集 (EditReportScreen) → 削除する
│       ├─ 編集 → 店舗編集 (EditShopScreen) → 削除する
│       └─ ♡アイコン → お気に入りトグル（即時反映、確認ダイアログなし）
├─ エリアカード「地図」ボタン → 店舗地図 (ShopsLocationScreen)
│                         └─ ピンタップ → 店舗詳細 (ShopScreen)
└─ エリアカード「一覧」ボタン（`ReportListButton`）→ 食レポ一覧 (HistoryScreen、**エリア**フィルタ付き。店舗一覧ではない)

⚠️ Android 版と共通の注意: エリアカードの「一覧」ボタンは AreaShopListScreen（店舗一覧）には遷移しない（`onAreaReportClick` → `goToHistoryWithAreaFiltering`）。店舗一覧を開くにはカード本体（ボタン以外の領域）をタップする。詳細は `android-ui-operator/recipe.md` 参照。

予定 (ScheduleScreen) … 全訪問予定一覧
└─ カードの 編集/削除 アイコン、食レポアイコンで AddReportScreen へ

食レポ (HistoryScreen) … 全食レポ一覧（年/月フィルタあり）
├─ [+] → 店舗選択なしでは登録不可（店名入力が必須、店舗未選択時は「ノート」経由を促す）
└─ カード長押し → 食レポ編集 (EditReportScreen)

設定 (SettingsScreen)
└─ バージョン表示、プライバシーポリシーリンクのみ（編集操作なし）
```

### 地図表示は Android と実装が異なる

`ShopsLocationScreen` の地図コンポーネントは expect/actual（`.claude/rules/platform-specific.md` 参照）。Android は Google Map、**iOS は Apple Maps（MapKit）** を表示する。UI 階層上はどちらも `Google Map` という content-desc が Android では付くが、iOS 版はネイティブの MapKit ビューなので `inspect_screen` で要素として捕捉できない場合がある（`take_screenshot` で目視確認する）。

## Maestro 特有の操作クセ（このアプリで実際にハマった点）

このアプリ（Compose Multiplatform 製、`resource-id`/`id` セレクタが使えない前提は `SKILL.md` 参照）を実際に操作して判明した、Maestro 固有の注意点。

1. **同じテキストが複数箇所にある場合、`tapOn: "text"` は意図しない要素に当たることがある**。
   例：AddShop/EditShop 画面はアプリバーのタイトルが「登録」、送信ボタンのラベルも「登録」で完全に同じ文字列。`tapOn: "登録"` は最初に見つかった要素（タイトル）に一致してしまい、ボタンを押せない。`inspect_screen` で対象要素の `b`（bounds）を確認し、**曖昧な場合は `point:` で座標指定するか `index:` で区別する**こと。

2. **空の TextField は `text:` セレクタで狙えない**（一致する文字列が存在しないため）。
   ラベルのテキスト（例：「名前」）をタップしてもフィールドにフォーカスが移らないことがある。`inspect_screen` でラベルの bounds を確認し、その少し下（または横）の空欄部分を `point:` でタップする。

3. **ソフトウェアキーボード表示中は画面下部のボタンが隠れて押せない**。`inputText` の直後にそのまま `tapOn` で下部のボタンを押そうとすると失敗する。キーボードを閉じる方法は何度も事故を起こしたので、**必ず以下の手順を踏むこと**：
   - `hideKeyboard` コマンドはこのアプリでは `Couldn't hide the keyboard` で失敗するため使わない
   - `tapOn: "完了"`（テキスト一致）は**信頼できない**。`inspect_screen` すると「完了」という `accessibilityText` を持つ要素が**同時に2つ存在することがある**：(1) IME の予測変換候補チップ（たまたま「完了」という単語が候補に出ているだけで、実際にはただの単語候補）、(2) 本当のキーボード確定/dismissボタン。前者はテキストだけを見ると区別がつかず、`tapOn: "完了"` は最初に見つかった方（＝予測変換候補）に一致してしまい、フォームに意図しない文字が入力される事故につながった
   - 「評価」など画面内の非インタラクティブなラベルをタップしてキーボードを閉じる方法や、ステータスバー付近をタップする方法は**状況によって効かないことがある**ため、単独の対策としては信頼しないこと
   - **最も確実な方法**：`inspect_screen` で `resource-id=Done` を持つ要素を探し、その `bounds` の中心座標を `point:` で直接タップする。この要素は日本語キーボードのチェックマークボタン・英語キーボードの `done` キーの両方に対応する実体で、予測変換候補と混同しない
     ```yaml
     - inputText: "..."
     # ここで inspect_screen し、resource-id=Done の要素の bounds を確認する
     - tapOn:
         point: "<Doneボタンのbounds中心座標>"
     - waitForAnimationToEnd      # キーボードのdismissアニメーション完了を待つ
     # 念のため inspect_screen して、入力した値が意図通りか（余分な文字が混入していないか）を確認してから次に進む
     - tapOn: "送信ボタンのテキスト"
     ```
   - 特に日本語入力後は、**キーボードを閉じた直後に対象フィールドの `val`/`text` を `inspect_screen` で再確認する**習慣をつけること。誤ったキーを踏んで1文字混入していても、`inspect_screen` を都度取らないと気づかない

4. **AddShopScreen の AI 情報取得ダイアログで入力した店名は、本体フォームの「名前」欄に自動反映されない**（Android 版は反映される、iOS 版特有の挙動）。ダイアログでの「店舗情報を取得」実行後、**必ず本体フォームの「名前」欄にも同じ店名を手動入力**してから登録すること。忘れると「名前」が空のまま「登録」ボタンが非活性になる。

5. **長押し（`longPressOn`）はテキストの前後に見えない空白が含まれると失敗する**ことがある（`inspect_screen` の `txt` をよく見ると末尾に半角スペースが付いている場合がある）。`Element not found` になったら `point:` にフォールバックする。

6. **DatePicker（Compose Multiplatform 製、iOS ネイティブの UIDatePicker ではない）の日付セルは日番号だけでなく完全な読み上げ用ラベルを持つ**。例：19日は `"19"` ではなく `"今日, 2026年9月19日 土曜日"`。`tapOn: "19"` は一致せず失敗するので、`inspect_screen` で得た完全な文字列（曜日・今日ラベル込み）をそのまま使うこと。

7. **`maestro test <flow.yaml>` の `takeScreenshot` コマンドは出力先が Maestro のテスト実行フォルダ配下に制限される**（`/tmp` 等を直接指定すると `resolves outside this run's takeScreenshot output folder` で失敗）。単純に目視確認したいだけなら `xcrun simctl io "<device>" screenshot <path>` を使う方が制約がなく簡単。

8. **macOS の BSD `sort -u` は日本語（マルチバイト文字）のロケール依存で重複排除に失敗し、異なる文字列を誤って同一視して欠落させることがある**。`maestro hierarchy --compact` の出力から一覧を作る際は `sort -u` ではなく `awk '!seen[$0]++'` を使うこと。

## 機能別確認レシピ

### エリア CRUD（ノートタブ）

1. **登録**: ノート画面右下 `[+]` → 「エリア名を入力してください」フィールドに入力 → 「登録する」
   - iOS では Unsplash 画像取得は問題なく成功する（Android の開発環境で見られたエラーダイアログは発生しなかった。2026-09-19 時点確認）
2. **編集**: エリアカードを長押し → エリア名変更 / 画像変更 / 削除
3. **並び替え**: 「並び替え」ボタン → 各エリアの表示順を数値で指定 → 「変更する」
   - ⚠️ 数値フィールドをタップしてキーボードで直接入力すると、ソフトウェアキーボードが下部の
     「変更する」ボタンを隠して押せなくなることがある（クセ3参照）。各行に併設された
     「+」「−」ステッパーボタンを使えばキーボードを開かずに値を変更できるため、これを推奨する
4. **削除**: 編集画面の「削除する」→ 確認ダイアログ「はい」

### 店舗 CRUD（ノート → エリアカード本体タップ → エリア店舗一覧）

1. **登録**: エリア店舗一覧の `[+]` → 「店名」入力 → 「店舗情報を取得」ボタンで Gemini AI が系統・地図URL・最寄り駅・ノート等を自動生成
   - AI 生成値かどうかの判定は Android 版と同じ（`ShopAiInfo` のデフォルト値は空文字）
   - **iOS 特有の注意（上記クセ4）**: ダイアログで入力した店名は本体フォームの「名前」欄に自動で入らない。ダイアログを閉じた後、改めて「名前」欄に店名を入力すること
   - App Check: この `iPhone 17` シミュレータでは 2026-09-19 時点で AI 呼び出しが成功することを確認済み（追加のデバッグトークン登録は不要だった）。別のシミュレータ/実機で失敗する場合は `.claude/rules/ai-implementation.md` を参照
   - キーボードが出ている状態のまま「登録」ボタン（画面下部）をタップしても反応しないことがある。上記クセ3の手順でキーボードを閉じてから押すこと
2. **詳細確認**: 店舗タップ → エリア・Webサイト・地図・評価・最寄り駅・系統・ノートを表示。上部 `♡` でお気に入り即時トグル
3. **編集**: 店舗詳細の「編集」→ 各項目を変更（スクロールが必要）→ 「編集する」
4. **削除**: 編集画面をスクロールして「削除する」→ 確認ダイアログ「はい」

### 食レポ CRUD（店舗詳細 → 🍜追加、または 食レポタブ）

1. **登録**: 店舗詳細の 🍜追加（2つ並ぶ `追加` ボタンの2番目）→ 日付選択（デフォルト当日）、評価、メニュー情報、感想 → 「レポートする」
   - 「感想」欄は空のTextFieldなのでクセ2の通り座標タップが必要な場合がある
   - 「レポートする」ボタンはキーボードに隠れやすい。クセ3の手順（`resource-id=Done` の要素を座標タップ → `waitForAnimationToEnd`）でキーボードを閉じ、`inspect_screen` で入力値に余分な文字が混入していないか確認してから押すこと
2. **一覧確認**: 店舗詳細の 🍜一覧、または食レポタブ（全件、年/月フィルタ付き）
3. **編集/削除**: 食レポカードを長押し（クセ5：失敗したら座標にフォールバック）→ EditReportScreen → スクロールして「削除する」

### 訪問予定 CRUD（店舗詳細の📋、または 予定タブ）

1. **登録**: 店舗詳細の 📋追加（2つ並ぶ `追加` ボタンの1番目。既に予定がある場合はラベルが「変更」になる）→ DatePicker で日付選択（クセ6：完全なラベル文字列でタップ）→ OK で即時登録（「スケジュールを作成しました」ダイアログが出る）
2. **一覧確認**: 予定タブで全件確認（日付・店舗名・食レポ/編集/削除アイコン）
3. **削除**: 予定タブのカードの「削除」→ 確認ダイアログ「はい」

### 地図表示（ノート → エリアカード「地図」）

- iOS は Apple Maps（MapKit）でエリア内の店舗をピン表示。ピンタップで店舗詳細へ遷移
- ネイティブ地図ビューのため `inspect_screen` で要素を拾えないことがある。`take_screenshot`（または `xcrun simctl io screenshot`）で目視確認する

### 設定タブ

- 表示のみ（バージョン番号、プライバシーポリシーへのリンク）。Android 版と同一のバージョン表示（`androidApp`/`iosApp` は同じ `versionName`/`MARKETING_VERSION` で管理、`.claude/rules/dependencies.md` 参照）
