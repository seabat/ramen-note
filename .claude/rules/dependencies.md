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
./gradlew :androidApp:assembleDebug ktlintCheck :androidApp:testDebugUnitTest \
          :sharedUI:generatePreviewGallery \
          :sharedUI:linkDebugFrameworkIosSimulatorArm64 --rerun-tasks
```

- **iOS のリンクまで通すこと**。Android だけでは klib 非互換・SKIE の問題を検出できない
- `--rerun-tasks` を付けないとキャッシュで警告が出ず、deprecation の見落としにつながる

## その他の対応関係

- **AGP ↔ Gradle**: AGP 9.3 以降は **Gradle 9.5 以上**が必須。AGP を上げたら
  `gradle/wrapper/gradle-wrapper.properties` の `distributionUrl` も更新する。
  なお AGP が Gradle 要件を満たさない状態では `./gradlew wrapper` タスク自体が失敗するため、
  `distributionUrl` を直接書き換える。
- **Room ↔ androidx.sqlite**: Room の POM が参照する `sqlite` バージョンを確認する。
- **モデル・AI SDK**: Firebase AI 関連は `.claude/rules/ai-implementation.md` を参照。
