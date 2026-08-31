# 依存バージョン管理

**バージョンの唯一の正は `gradle/libs.versions.toml`。** 他のファイル（CLAUDE.md 等）に
バージョン番号を転記しない（同期されず陳腐化するため）。README の「開発環境」は人間向けの
参考情報として主要バージョンのみ記載する。

## 上げられないバージョン（固定理由つき）

いずれも**カタログを読んでも分からない制約**。安易に最新へ上げると壊れる。

| ライブラリ | 固定値 | 上限の理由 |
|---|---|---|
| `kotlin` | **2.3.21** | 2.4.x に上げると KSP が連鎖して壊れる（下記 `ksp` 参照）。2.3 系の最新が 2.3.21 |
| `ksp` | **2.3.9** | compose-nav-graph 0.2.1 が KSP **2.3.10 以降**で `The 'ksp' configuration is deprecated in Kotlin Multiplatform projects` を出して**プラグイン適用時に失敗**する |
| `coil` | **3.4.0** | 3.5.0 以降の klib が Kotlin 2.4 系でビルドされており（`abi_version=2.4.0`）、Kotlin 2.3.21 では **iOS の klib 解決に失敗**する |

### ⚠️ Coil の罠

Coil を上げても **Android ビルドは成功する**。壊れるのは iOS の
`compileKotlinIosSimulatorArm64` / `compileKotlinIosArm64` で、次のエラーになる。

```
e: KLIB resolver: Could not find ".../coil-iosSimulatorArm64Main-3.6.0.klib" in [...]
```

ファイルは存在するのにこのメッセージが出る場合、原因は「見つからない」ではなく
**Kotlin コンパイラのバージョン不整合**。Android だけ確認して通すと見逃す。

### 連鎖関係

```
compose-nav-graph 0.2.1  →  ksp ≤ 2.3.9  →  kotlin 2.3.x  →  coil ≤ 3.4.0
                                                          →  KMP ライブラリ全般が
                                                             Kotlin 2.3 系ビルドである必要
```

Kotlin を 2.4 系へ上げるには **compose-nav-graph 側の KSP 対応**が先に必要。

## バージョン更新時の手順

### 1. 最新の安定版を調べる

Maven のメタデータを直接見る（`latest` にはプレレビューが入るので安定版を選ぶこと）。

```bash
# Maven Central
curl -s https://repo1.maven.org/maven2/<group を / 区切り>/<artifact>/maven-metadata.xml \
  | grep -o '<version>[^<]*' | sed 's/<version>//' \
  | grep -Ev 'alpha|beta|rc|RC|Beta|dev|M[0-9]|eap' | tail -5
# Google Maven は https://dl.google.com/dl/android/maven2/ に差し替え
```

### 2. KMP ライブラリは klib のビルド Kotlin を確認する

`kotlin` を上げずに KMP ライブラリだけ上げる場合、その klib が**どの Kotlin でビルドされたか**を
事前に確認すると iOS ビルドの失敗を防げる。

```bash
curl -sfo x.klib "https://repo1.maven.org/maven2/<path>/<artifact>-iossimulatorarm64/<ver>/<file>.klib"
unzip -oq x.klib -d k && find k -name manifest -exec grep -E 'compiler_version|abi_version' {} \;
```

`compiler_version` がプロジェクトの `kotlin` より新しいメジャー/マイナーなら**使えない**。

### 3. 検証（iOS を必ず含める）

```bash
./gradlew ktlintFormat
./gradlew :androidApp:assembleDebug ktlintCheck \
          :sharedLogic:allTests :sharedUI:allTests \
          :sharedUI:generatePreviewGallery \
          :sharedUI:linkDebugFrameworkIosSimulatorArm64 --rerun-tasks
```

- **iOS のリンクまで通すこと**。Android だけでは klib 非互換・SKIE の問題を検出できない
- **テストは `allTests` を使うこと**。テストは `sharedLogic` / `sharedUI` の `commonTest` にあり、
  `:androidApp:testDebugUnitTest` は **NO-SOURCE で何も実行しない**（過去にこれが原因で
  テストの破損が長期間検出されなかった）
- `--rerun-tasks` を付けないとキャッシュで警告が出ず、deprecation の見落としにつながる

## AGP 9 新 DSL（`android.newDsl` / `android.builtInKotlin`）

**この2フラグは連動しており、片方だけ有効にすると必ず失敗する。**

```
android.builtInKotlin=true
android.newDsl=true
```

- `newDsl=true` だけにすると `org.jetbrains.kotlin.android` プラグインの適用時に
  `ApplicationExtensionImpl cannot be cast to BaseExtension` で**ビルドが落ちる**
- `builtInKotlin=true` にしたら、各モジュールの `plugins {}` から
  `alias(libs.plugins.kotlinAndroid)` を**削除する**（AGP 組み込みの Kotlin を使うため）
- 旧 DSL のままだと `Project.android(...)` と `org.jetbrains.kotlin.android` の
  deprecation 警告が出続ける

なお AGP のバージョン更新時に `android.*` の互換フラグが増えることがある。
`The option setting 'android.xxx=yyy' is deprecated` と出たものは削除してよいが、
**リリースビルドの出力に影響しないことを APK の中身で確認する**こと
（`unzip -l` のエントリ名・サイズ一覧を削除前後で比較する。R8・リソース圧縮系の
フラグはデバッグビルドでは差が出ない）。

## iOS の Swift Package（SPM）が壊れたときの対処

Xcode ビルドが次のように**全プロダクトまとめて**失敗することがある。

```
Missing package product 'FirebaseAILogic'
Missing package product 'FirebaseCrashlytics'
Missing package product 'FirebaseAI'
Missing package product 'FirebaseAnalytics'
Missing package product 'FirebaseAppCheck'
```

**5つ同時に出るのは、個々のプロダクト名の誤りではなくパッケージグラフ全体の
ロード失敗**（DerivedData 内の SPM 状態の不整合）。まず解決をやり直す。

```bash
xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp -resolvePackageDependencies
```

Xcode GUI なら **File → Packages → Resolve Package Versions**、
それでも直らなければ **Reset Package Caches**。

### 誤診しないための切り分け

- **`project.pbxproj` の差分を見る。** バージョン番号（`MARKETING_VERSION` /
  `CURRENT_PROJECT_VERSION`）しか変わっていないなら、その変更は原因ではない
- **プロダクト名は Package.swift で実在を確認する。** 例えば `FirebaseAI` と
  `FirebaseAILogic` は**両方とも実在する**別プロダクトで、併記されていても誤りではない

```bash
curl -s https://raw.githubusercontent.com/firebase/firebase-ios-sdk/<ver>/Package.swift \
  | grep -A2 '\.library(' | grep 'name:'
```

- **`Package.resolved` は git 管理下**（`iosApp/iosApp.xcodeproj/project.xcworkspace/
  xcshareddata/swiftpm/Package.resolved`）。解決し直して差分が出たらコミット要否を判断する

### Gradle の iOS 検証では検出できない

`:sharedUI:linkDebugFrameworkIosSimulatorArm64` は Kotlin 側の klib・SKIE を検証するが、
**SPM の解決は Xcode 側の処理なので通過しない**。SPM 起因の失敗は Xcode ビルドでしか出ない。

```bash
# Xcode ビルドまで確認する場合
xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp -configuration Debug \
  -destination 'platform=iOS Simulator,name=iPhone 17' build
```

## その他の対応関係

- **AGP ↔ Gradle**: AGP 9.3 以降は **Gradle 9.5 以上**が必須。AGP を上げたら
  `gradle/wrapper/gradle-wrapper.properties` の `distributionUrl` も更新する。
  なお AGP が Gradle 要件を満たさない状態では `./gradlew wrapper` タスク自体が失敗するため、
  `distributionUrl` を直接書き換える。
- **Room ↔ androidx.sqlite**: Room の POM が参照する `sqlite` バージョンを確認する。
- **モデル・AI SDK**: Firebase AI 関連は `.claude/rules/ai-implementation.md` を参照。
