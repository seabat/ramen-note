---
name: version-increment
description: Android / iOS アプリのバージョン（versionName / versionCode）を更新してコミットする
disable-model-invocation: true
allowed-tools: Bash, Read, Edit
---

# バージョンインクリメント

Android と iOS のアプリバージョンを同じ値に更新し、コミットする。

**使い方**

```
/version-increment 1.4.0    # versionName を明示指定
/version-increment          # マイナー +1 案を提示して確認
```

---

## 対象ファイルとキー

バージョンの実体は以下の **2 ファイル・6 箇所**のみ。ここ以外は書き換えない。

| ファイル | キー | 箇所数 |
|---|---|---|
| `androidApp/build.gradle.kts` | `versionCode` | 1 |
| `androidApp/build.gradle.kts` | `versionName` | 1 |
| `iosApp/iosApp.xcodeproj/project.pbxproj` | `CURRENT_PROJECT_VERSION` | 2（Debug / Release） |
| `iosApp/iosApp.xcodeproj/project.pbxproj` | `MARKETING_VERSION` | 2（Debug / Release） |

> **`iosApp/Configuration/Config.xcconfig` は対象外**。`baseConfigurationReference` から参照されてはいるが、
> pbxproj の `buildSettings` に直接値があるためそちらが優先され、xcconfig 側の値（`1` / `1.0`）は
> 実効しないデッド値になっている。触らないこと。

---

## ステップ 1: 現在バージョンの読み取りと整合チェック

4 キーすべてを読み取る。

```bash
grep -E 'versionCode|versionName' androidApp/build.gradle.kts
grep -E 'CURRENT_PROJECT_VERSION|MARKETING_VERSION' iosApp/iosApp.xcodeproj/project.pbxproj
```

**Android と iOS で値が食い違っていたら、その場で中断する。** 差分を以下の形式で提示し、
どう揃えるかをユーザーに委ねる（自動で片方に寄せない）。

```
android      : 1.3.1 (22)
ios(Debug)   : 1.3.1 (24)   ← ずれ
ios(Release) : 1.3.1 (24)   ← ずれ
```

一致していれば、その値を「現在バージョン」として次へ進む。

---

## ステップ 2: 新バージョンの決定

### 引数がある場合

引数を新しい `versionName` として採用する。以下を検査する。

- **形式**: `X.Y.Z`（数字とドットのみ。`1.4` や `v1.4.0` はエラーにして中断）
- **逆行**: 現在バージョン以下なら警告して続行可否を確認する（意図的な巻き戻しもありうるため禁止はしない）

### 引数がない場合

**マイナーバージョンを +1 し、パッチを 0 にリセットした案**を提示して確認を取る。

```
現在: 1.3.1 (22)
提案: 1.4.0 (23)  でよいですか？（y / 別の versionName を入力）
```

ユーザーが別の値を入力したら、上記と同じ形式・逆行の検査を行う。

### versionCode

**常に現在値 +1**。引数では受け付けない。

---

## ステップ 3: 前提チェック

以下に該当したら中断する。

1. **対象 2 ファイルに未コミットの変更がある場合**

   ```bash
   git diff --name-only -- androidApp/build.gradle.kts iosApp/iosApp.xcodeproj/project.pbxproj
   ```

   出力があれば `git diff` の内容を提示して中断する。そのままコミットすると
   無関係な編集内容を巻き込むため。
   （対象外のファイルに変更があるのは問題ない。ステップ 5 のパス指定コミットで除外される）

2. **ブランチが `main` の場合**

   ```bash
   git rev-parse --abbrev-ref HEAD
   ```

   `main` なら「main 上ですが続行しますか？」と確認する。それ以外のブランチはそのまま進む
   （ブランチを切る／切り替えるのはこのスキルの責務ではない）。

---

## ステップ 4: 書き換えと検算

キーを明示した置換で 6 箇所を書き換える。`NEW_NAME` / `NEW_CODE` は決定した値。

```bash
sed -i '' -E "s/versionCode = [0-9]+/versionCode = ${NEW_CODE}/" androidApp/build.gradle.kts
sed -i '' -E "s/versionName = \"[^\"]+\"/versionName = \"${NEW_NAME}\"/" androidApp/build.gradle.kts
sed -i '' -E "s/CURRENT_PROJECT_VERSION = [0-9]+;/CURRENT_PROJECT_VERSION = ${NEW_CODE};/" iosApp/iosApp.xcodeproj/project.pbxproj
sed -i '' -E "s/MARKETING_VERSION = [0-9.]+;/MARKETING_VERSION = ${NEW_NAME};/" iosApp/iosApp.xcodeproj/project.pbxproj
```

**必ず箇所数を検算する。** 期待値と 1 件でも違えば中断して報告する。

```bash
grep -c "versionCode = ${NEW_CODE}" androidApp/build.gradle.kts                        # 期待 1
grep -c "versionName = \"${NEW_NAME}\"" androidApp/build.gradle.kts                    # 期待 1
grep -c "CURRENT_PROJECT_VERSION = ${NEW_CODE};" iosApp/iosApp.xcodeproj/project.pbxproj  # 期待 2
grep -c "MARKETING_VERSION = ${NEW_NAME};" iosApp/iosApp.xcodeproj/project.pbxproj        # 期待 2
```

ビルドは実行しない（数値の書き換えのみのため検算で十分）。

---

## ステップ 5: コミット

`git diff` を提示してから、**必ずパス指定で**コミットする。

```bash
git commit -m "バージョンを ${NEW_NAME} (${NEW_CODE}) に更新" \
  -- androidApp/build.gradle.kts iosApp/iosApp.xcodeproj/project.pbxproj
```

> `--` 以降はすべてパスとして解釈されるため、**`-m` は必ず `--` より前に置く**こと。
> 逆順にすると `-m` とメッセージ本文がパス名とみなされ `pathspec ... did not match` で失敗する。

- **メッセージは件名 1 行のみ。** `Co-Authored-By` や `Claude-Session` などのトレーラーは付けない
- **パス指定は必須。** 理由は下記の Hook 注意を参照

### ⚠ PreToolUse Hook との共存

`.claude/settings.json` の PreToolUse Hook が `git commit` を含むコマンドを検知すると、
コミット前に自動で以下を実行する。

```bash
./gradlew ktlintFormat --rerun-tasks && git add -u
```

このため次の 3 点を守る。

- **コミットに数十秒〜数分かかる**旨を実行前にユーザーへ伝える（Gradle がフルで走るため）
- **パス指定コミットを必須にする**。`git add -u` が追跡中の全変更をステージするが、
  パス指定コミットはインデックスを参照しないため、コミット内容には混入しない
- **コミット後の `git status` は報告しない**。Hook がステージした無関係なファイルが並ぶだけでノイズになる

なお本スキルは Kotlin ファイルを変更しないため、`ktlintFormat` が差分を生むことはない。

---

## ステップ 6: 完了

結果を報告して終了する。**push や PR 作成は行わない。**

```
バージョンを 1.4.0 (23) に更新しました。
push / PR 作成は /pr-create で行ってください。
```

---

## 関連スキル

- `/release-prep` — リリースノート作成など、リリース前の準備作業
- `/pr-create` — PR の作成
