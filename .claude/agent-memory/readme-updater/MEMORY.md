# README 更新エージェント メモリ

## README.md の構造（セクション順）
1. タイトル・概要・スクリーンショット・ダウンロード・主な機能（管轄外・触らない）
2. 技術スタック（フレームワーク・ライブラリ／開発環境）
3. プロジェクト構造
4. セットアップ（管轄外・触らない）
5. `## Claude Code` セクション
   - プロジェクト設定（CLAUDE.md）
   - `### スキル（Skills）` … 表
   - `### サブエージェント（Agents）` … 表 + regression-reviewer のチェック項目表・追記方法
   - `### Hooks` … 表
6. ライセンス・参考リンク（管轄外・触らない）

## 記述ルールの実例
- スキル表: `| /skill-name | 説明（1〜2文） |` 列名は「スキル」「説明」。行の並びはアルファベット順が既存慣習（pr-create → release-prep のように）。
- サブエージェント表: 列名は「エージェント」「説明」。トリガ条件がある場合は説明文の後半に「〜のとき自動的に起動を促す」という形で追記する（regression-reviewer の書き方が典型例）。
- Hooks 表: 列名は「タイミング」「処理」。タイミングは `` `EventName` 前 `` / `` `EventName` 後 `` のように角括弧付きイベント名 + 前後を明記。PostToolUse のような条件分岐が複数ある hook は1行にまとめ、「条件 → 促す内容／条件 → 促す内容」の形式で列挙する。

## 検証方法（実データ裏取り）
- バージョン系: `gradle/libs.versions.toml`（唯一の正）、`androidApp/build.gradle.kts`（versionCode/versionName/compileSdk等）、`gradle/wrapper/gradle-wrapper.properties`（Gradle）、`gradle.properties`（AGP新DSLフラグ）を直接読む。
- テスト件数: `grep -rn "@Test" sharedLogic/src/commonTest | wc -l` / 同 sharedUI で実数カウントできる。実際に `./gradlew :sharedLogic:allTests :sharedUI:allTests` を実行して BUILD SUCCESSFUL を確認するのが最も確実（15秒程度で終わる）。`:androidApp:testDebugUnitTest` は必ず NO-SOURCE になる（androidApp にテストソースがないため）。
- NavGraph Previews件数: `./gradlew :sharedUI:generatePreviewGallery` を実際に実行すると `renderNavGraphGalleryLayoutlib` のログに `layoutlib rendering N preview(s)` と出るので、README記載件数と突合できる（1回の実行で確定）。
- `./gradlew ktlintCheck` は1秒未満で終わるので毎回実際に実行して確認してよい。
- Google Maps機能（ShopsLocationScreen）のように、技術スタック表に載っていない依存が libs.versions.toml に存在することがある（例: mapsCompose）。`grep -rn "implementation(libs\." sharedUI/build.gradle.kts sharedLogic/build.gradle.kts` で実際の依存一覧を洗い出し、README技術スタックとの差分を確認する習慣をつけること。
- セットアップ手順（Unsplash/Firebase/Google Maps等の APIキー設定）は本エージェントの通常スコープ外（ユーザー手動管理）だが、ユーザーが明示的に「セットアップ手順も実装と照合してほしい」と依頼した場合はスコープに含めて確認・修正してよい。その場合、`sharedLogic/build.gradle.kts` の `generateBuildSecrets` タスク定義（`props.getProperty(...)` の一覧）が BuildSecrets に生成される全キーの正なので、そこと README のAPIキー設定セクションを突合する。

## 既知の注意点
- モジュール分割（`composeApp` → `sharedLogic` / `sharedUI` / `androidApp`）以前の古いパス表記が、`.claude/` 配下のあちこちに取り残されていたことがある（`/release-prep` スキル、`readme-updater.md`、`regression-reviewer.md`。いずれも修正済み）。README 以外のファイルでも古いパスに気づいたらユーザーに報告すること。
- `.claude/skills/` と `.claude/agents/` は実ファイルを ls して都度確認すること。README に載っていないスキル（例: android-device-interactor）が存在することがあるため、ユーザーの指示に挙がっていない項目でも実態と照合して漏れがあれば報告・追記する。
- サブエージェントがスキルに「格下げ」されるケース（例: icon-replacer）がある。エージェント一覧から削除し、スキル一覧に追加する対応が必要。
- `.claude/settings.json` の hooks 配列は `PreToolUse` / `PostToolUse` / `Stop` などイベント種別ごとに分かれており、`matcher` でツール名、`hooks[].command` に実処理（shell + jq でファイルパスやコマンド文字列を判定）が書かれている。PostToolUse は `additionalContext` を jq で組み立てて返す形式（リマインダ表示）になっていることがある。
- README の「技術スタック」「プロジェクト構造」セクションは `.claude/CLAUDE.md` の記述（例: sharedLogic/sharedUI 構成）と乖離している場合があるが、これはユーザー指示がない限り本エージェントの今回のスコープ外（ユーザーが明示的にスキル/エージェント/Hooks 章のみ指示するケースがある）。ただし一般タスクとしてはステップ1〜2で全セクション照合すべき。
- 変更はセクション単位で Edit の old_string/new_string を絞り、無関係な行は一切触らない。
