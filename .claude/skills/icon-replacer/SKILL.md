---
name: icon-replacer
description: ramen-note の Android / iOS アプリアイコン・スプラッシュ・タスクスイッチャーオーバーレイを一括で差し替える。開発者が新しいアイコン画像を用意し、明示的に `/icon-replacer` で実行する。content_image・transparent_image・bg_color の3つを入力として受け取る。
---

# アイコン差し替えスキル

Android・iOS 両プラットフォームのアプリアイコン、スプラッシュスクリーン、タスクスイッチャーオーバーレイを一括で差し替える。すべての出力は日本語で行う。

> **このスキルは開発者が明示的に実行する**もの（`/icon-replacer`）。3つの入力パラメータと Android Studio での事前生成が必要なため、Claude が実装中に自動起動する類の作業ではない。
>
> **対象範囲**: 画像・背景色の「差し替え」のみ。SplashScreen API の導入、`AndroidManifest.xml` のテーマ設定、`AppIcon.appiconset/Contents.json` の構造定義などの**初回セットアップは完了済み**の前提で動作する。初回セットアップが未実施の場合は `update_icon` ブランチのコミット履歴を参照。

## 前提：開発者が事前に行う作業

1. **コンテンツ画像**（1024×1024 PNG、背景あり）を用意
2. **背景透過画像**（1024×1024 PNG、背景透過）を用意
3. **背景カラーコード**（例: `#FBF2E9`）を用意
4. **Android Studio の Image Asset** でランチャーアイコンを生成
   - Icon type: `Launcher Icons (Adaptive and Legacy)`
   - Foreground Layer: 背景透過画像 / Background Layer: Color → 背景カラーコード
   - Options: Legacy Icon = No、Round Icon = No、Icon Format = PNG
   - **出力先（Res Directory）を必ず `androidApp/src/main` に変更する**（デフォルトは `androidApp/src/main` のはずだが要確認）
   - 生成先: `androidApp/src/main/res/mipmap-*/ic_launcher_foreground.png`

## 入力パラメータ

| パラメータ | 説明 | 例 |
|---|---|---|
| `content_image` | コンテンツ画像のパス（背景あり 1024×1024） | `/Users/ryouta/Desktop/ramen_icon.png` |
| `transparent_image` | 背景透過画像のパス（1024×1024） | `/Users/ryouta/Desktop/ramen_icon_transparent.png` |
| `bg_color` | 背景カラーコード（`#RRGGBB`） | `#FBF2E9` |

**呼び出し例:**

```
/icon-replacer
content_image: /Users/ryouta/Desktop/ramen_icon.png
transparent_image: /Users/ryouta/Desktop/ramen_icon_transparent.png
bg_color: #FBF2E9
```

## 作業手順

### ステップ 0: 入力確認

1. `content_image`、`transparent_image` が実在するか `ls` で確認
2. `bg_color` が `#RRGGBB` 形式か確認（`#` なしなら補完）
3. 不正な場合は中断してユーザーに確認

### ステップ 0.5: Android Studio Image Asset の完了確認

以下をユーザーに確認する（「はい」まで次に進まない）:

> Android Studio の Image Asset で `ic_launcher_foreground.png` の生成は完了していますか？
> （Icon type: Launcher Icons(Adaptive and Legacy)、Foreground: 背景透過画像、Background: bg_color、Legacy Icon: No、Round Icon: No、PNG形式）

確認後、各 density の生成物が本日付で更新されているか検証する:

```bash
for d in mdpi hdpi xhdpi xxhdpi xxxhdpi; do
  ls -lh androidApp/src/main/res/mipmap-$d/ic_launcher_foreground.png
done
```

- 全 density のファイルが存在し、タイムスタンプが最近であることを確認
- 欠損・古いタイムスタンプがあれば中断してユーザーに再確認

### ステップ 1: 色変換（iOS 用）

`bg_color` を iOS の `Color(red:green:blue:)` 形式（0〜1）に変換する。

```bash
python3 -c "
c = '#RRGGBB'  # bg_color に置換
r = int(c[1:3], 16) / 255; g = int(c[3:5], 16) / 255; b = int(c[5:7], 16) / 255
print(f'r={r:.3f}, g={g:.3f}, b={b:.3f}')
print(f'red={r:.17f}'); print(f'green={g:.17f}'); print(f'blue={b:.17f}')  # storyboard 用高精度値
"
```

### ステップ 2: Android 画像ファイルの差し替え

| 差し替え先 | 使用する画像 |
|---|---|
| `androidApp/src/main/res/drawable-nodpi/ic_splash_raw.png` | `transparent_image` |

> ⚠️ 旧手順にあった `ic_launcher-playstore.png` は現プロジェクトに存在しない（廃止された可能性）。存在する場合のみ `content_image` で上書きし、無ければスキップする。実行前に `find . -name 'ic_launcher-playstore.png'` で確認すること。

### ステップ 3: Android XML の色更新

**`androidApp/src/main/res/values/ic_launcher_background.xml`**
```xml
<color name="ic_launcher_background">#RRGGBB</color>
```
→ `#RRGGBB` を `bg_color` に書き換える。

**`androidApp/src/main/res/values/themes.xml`**
```xml
<item name="windowSplashScreenBackground">#RRGGBB</item>
```
→ `#RRGGBB` を `bg_color` に書き換える。

### ステップ 4: iOS 画像ファイルの差し替え

| 差し替え先 | 使用する画像 |
|---|---|
| `iosApp/iosApp/Assets.xcassets/AppIcon.appiconset/ic_app_icon.png` | `content_image` |
| `iosApp/iosApp/Assets.xcassets/AppIcon.appiconset/ic_app_icon_transparent.png` | `transparent_image` |
| `iosApp/iosApp/Assets.xcassets/Logo.imageset/Logo.png` | `content_image` |
| `iosApp/iosApp/Assets.xcassets/SplashIcon.imageset/ic_app_icon_transparent.png` | `transparent_image` |

### ステップ 5: LaunchScreen.storyboard の背景色更新

`iosApp/iosApp/LaunchScreen.storyboard` の以下を更新する:

```xml
<color key="backgroundColor" red="R_VALUE" green="G_VALUE" blue="B_VALUE" alpha="1" colorSpace="custom" customColorSpace="sRGB"/>
```
`R/G/B_VALUE` をステップ 1 の高精度値に書き換える。

### ステップ 6: iOSApp.swift のオーバーレイ背景色更新

`iosApp/iosApp/iOSApp.swift` の以下を更新する:

```swift
Color(red: R_VALUE, green: G_VALUE, blue: B_VALUE)
```
`R/G/B_VALUE` をステップ 1 の小数値（3桁）に書き換える。

## 完了確認

- ✅ 変更したファイルの一覧
- ⚠️ 変更できなかったファイルがあれば理由とともに報告
- 開発者への次のアクション（Android Studio でのビルド確認、Xcode でのビルド確認）

## 注意事項

- `androidApp/src/main/res/mipmap-*/ic_launcher_foreground.png` は Android Studio で生成済みのため、このスキルでは変更しない
- `ic_splash_icon.xml`（`androidApp/src/main/res/drawable/`）と `themes.xml` の構造は変更しない。色だけ更新する
- `AppIcon.appiconset/Contents.json` と各 imageset の `Contents.json` は変更しない（画像ファイル名が変わらないため）
- 既存ファイルを削除しない。必ず上書きコピーする
