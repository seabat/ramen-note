---
name: regression-test-runner
description: ramen-note のリグレッションテストを Android / iOS の実機・シミュレータ上で実際に操作して実行する。test-case.md のテストケースを現在のコードと照合して自動で追加・更新した上で、android-ui-operator / ios-ui-operator を呼び出して検証し、結果を記録する。
disable-model-invocation: true
allowed-tools: Read, Write, Edit, Glob, Grep, Bash, Skill
---

# リグレッションテスト実行

`test-case.md`（本スキルと同じディレクトリ）に定義されたテストケースを、実機/シミュレータを
実際に操作して検証し、結果を記録する。すべての出力は日本語で行う。

このスキルは**手動呼び出し専用**（`disable-model-invocation: true`）。実機/シミュレータ操作を
伴う重い処理のため、他の変更をトリガーに自動起動することはない。

---

## 引数の解釈

呼び出し時の引数で対象範囲を絞り込める。

- 引数なし → 全プラットフォーム（Android・iOS）・全ケースが対象
- `android` / `ios` → 対象プラットフォームを限定
- カンマ区切りの数値（例 `3,5`）→ 対象 No. を限定
- プラットフォームと No. は併用可能（例 `ios 3,5` で iOS の No.3・5 のみ）

対象外のケースは今回スキップとし、結果列は変更しない。

---

## ステップ1: テストケースの棚卸し（自動追加・更新）

1. `test-case.md` を読み込む
2. 対象プラットフォームの画面構成を確認する
   - 画面一覧: `sharedUI/src/commonMain/kotlin/dev/seabat/ramennote/ui/screens/` 配下の `*Screen.kt`
   - 画面遷移・機能別の詳細: `android-ui-operator/recipe.md`、`ios-ui-operator/recipe.md`
3. test-case.md に記載済みの No. が対応する機能と突き合わせ、新規画面・新規 CRUD 機能で対応する
   ケースがなければ、既存の表記（No. / 目的 / 手順 / 期待 / 結果）に倣って追加する。既存ケースの
   手順が実装と乖離していれば更新する
   - `.claude/rules/regression-patterns.md` に新たな `CHECK-N` が追記されていれば、対応する
     専用ケースが test-case.md にあるか確認し、なければ追加する
   - Android と iOS で同一シナリオは同じ No. で対応させる（片方にしかない機能は欠番でよい）
4. 追加・更新した内容があれば `test-case.md` を保存し、内容をステップ6の最終報告に含める。
   変更がなければそのまま次へ進む
   - **ユーザーへの事前確認は不要**。自律的に追加・更新して実行まで進め、事後にまとめて報告する

---

## ステップ2: 実行対象の決定

引数の解釈に従い、実行するプラットフォームと No. の一覧を確定する。

---

## ステップ3: ビルド・起動

対象プラットフォームごとに、実行前に必ずアプリをビルド・インストール・起動する
（`android-ui-operator` / `ios-ui-operator` の操作ループに入る前に完了させておく）。

### Android

```bash
adb devices
./gradlew :androidApp:installDebug
adb shell monkey -p dev.seabat.ramennote -c android.intent.category.LAUNCHER 1
```

### iOS

```bash
xcrun simctl list devices booted   # 起動中デバイスを確認。0台なら iPhone 17 を起動、複数ならユーザーに確認
xcrun simctl boot "iPhone 17"      # 対象デバイスを確定してから起動（起動済みならエラーは無視）
open -a Simulator
osascript -e 'tell application "Simulator" to activate' \
  -e 'tell application "System Events" to tell process "Simulator" to perform action "AXRaise" of (first window whose name contains "iPhone 17")'

xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp -configuration Debug \
  -destination 'platform=iOS Simulator,name=iPhone 17' build
APP_PATH=$(find ~/Library/Developer/Xcode/DerivedData/iosApp-*/Build/Products/Debug-iphonesimulator -maxdepth 1 -name "*.app")
xcrun simctl install "iPhone 17" "$APP_PATH"
xcrun simctl launch "iPhone 17" dev.seabat.ramennote
```

デバイス選択の詳細・複数起動時の確認手順は `ios-ui-operator/SKILL.md` の「デバイス選択」を、
SPM 解決に失敗した場合は `.claude/rules/dependencies.md` の該当セクションを参照する。

---

## ステップ4: 各ケースの実行

対象の各 No. について、以下を1件ずつ実施する。

1. `test-case.md` の「手順」に記載された操作を、Skill ツールで該当プラットフォームのスキル
   （Android は `android-ui-operator`、iOS は `ios-ui-operator`）を呼び出して実行する。
   実際の要素特定・タップ座標・キーボード操作等のプラットフォーム固有の作法は、呼び出し先の
   スキルとその `recipe.md` の判断に委ねる（本スキルでは再定義しない）
2. 実行結果を「期待」と照合し、PASS / FAIL を判定する
3. **前提条件となるケース（登録系など）が FAIL した場合**、それに依存する後続ケース
   （編集・削除等）は実施せず `SKIP（前提条件未成立: No.X FAIL）` として記録する
4. FAIL したケースのみ、証跡としてスクリーンショットを取得する
   （Android: `android screen capture`、iOS: `take_screenshot` または
   `xcrun simctl io <device> screenshot`）。本スキルと同じディレクトリの `screenshots/` 配下に
   `<プラットフォーム>_<No>_<実行日 YYYYMMDD>.png` として保存する（ディレクトリがなければ作成する）
5. 1件 FAIL・SKIP しても実行を中断せず、対象の全ケースを最後まで実施する

---

## ステップ5: 結果の記録

各ケースの「結果」列を、直近の実行結果のみで上書きする（履歴は保持しない）。

- PASS: `PASS (YYYY-MM-DD)`
- FAIL: `FAIL (YYYY-MM-DD): <失敗内容の要約>（[証跡](screenshots/<ファイル名>)）`
- SKIP: `SKIP (YYYY-MM-DD): 前提条件未成立（No.X FAIL）`

`test-case.md` を保存する。

---

## ステップ6: 実行サマリの報告

チャット上に以下を日本語でまとめる。

- 今回追加・更新したテストケース（ステップ1で変更があった場合。なければ「変更なし」と明記）
- 対象範囲（プラットフォーム・No.）
- 結果件数（PASS / FAIL / SKIP）
- FAIL・SKIP したケースの一覧（No. / 目的 / 失敗内容 / 証跡パス）

出力フォーマット例:

```
## リグレッションテスト実行結果（2026-09-23）

### テストケースの更新
- Android No.28 を追加（新規画面 XxxScreen 対応）

### 実行結果（Android 全27件）
PASS: 24 / FAIL: 1 / SKIP: 2

| No. | 目的 | 結果 |
|-----|------|------|
| 9 | 食レポ登録 | FAIL: 「レポートする」ボタンがキーボードに隠れて反応しない |
| 10 | 食レポ一覧表示 | SKIP: 前提条件未成立（No.9 FAIL） |
```
