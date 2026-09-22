# README 更新エージェント メモリ

## README.md の構造（セクション順）
1. タイトル・概要（管轄外）
2. スクリーンショット（管轄外）
3. ダウンロード（管轄外）
4. 主な機能（管轄外）
5. 技術スタック（フレームワーク・ライブラリ / 開発環境）★管轄
6. プロジェクト構造（主要なパッケージ構成含む）
7. セットアップ（管轄外。Unsplash/Google Maps/Firebase AppCheck/Firebase AI の設定手順を含む）
8. ビルドと実行・テスト・ktlint・NavGraph Graph ビュー
9. Claude Code
   - プロジェクト設定（CLAUDE.md）
   - スキル（Skills）★管轄
   - UI 操作系スキルの前提条件（android-ui-operator / ios-ui-operator）
   - サブエージェント（Agents）★管轄
   - Hooks ★管轄
10. ライセンス（管轄外）
11. 参考リンク（管轄外）

## 照合のポイント
- エージェント一覧・スキル一覧は `.claude/agents/*.md` / `.claude/skills/*/SKILL.md` のディレクトリ数と
  README テーブルの行数がまず一致するか確認する（追加・削除の検出が早い）。
- 各エージェント定義ファイル末尾の「Persistent Agent Memory」セクションのパス表記
  （`.claude/agent-memory/<name>/`）は、README のエージェントテーブル説明文にも同じ相対パスで
  記載されている。README は元々プロジェクトルート相対パスで統一されており、絶対パス表記は
  一度も使われていない（2026-09-22 時点で `/Users/ryouta` 等の絶対パスは README 内に0件）。
  → エージェント定義ファイル側の絶対パスを相対パスに修正しても、README 側に波及する変更は
     基本的に発生しない（README が先に正しい書き方をしていたケース）。
- Hooks テーブルは `.claude/settings.json` の `hooks` 配列のイベント数（PreToolUse×3, Stop, PostToolUse
  など）と README の行数を数で突き合わせるとよい。
- バージョン情報の正は `gradle/libs.versions.toml`（kotlin, agp, composeMultiplatform,
  android-compileSdk/minSdk/targetSdk）と `gradle/wrapper/gradle-wrapper.properties`
  （distributionUrl の Gradle バージョン）。`androidApp/build.gradle.kts` 自体にはバージョン番号は
  ほぼ書かれておらず `libs.versions.xxx.get()` 経由で参照しているだけなので、実際の数値は
  カタログ側を見る必要がある。
- `gradle.properties` の `android.newDsl` / `android.builtInKotlin` の有効・無効も README の
  「開発環境」セクション末尾の説明文と対応しているので変更時は要確認。

## 直近の確認結果（2026-09-22）
- スキル5件・エージェント3件・Hooks5件・バージョン情報すべて README と実態が一致していることを確認済み。
  次回もこの単位（スキル数／エージェント数／Hooksイベント数／libs.versions.toml の主要バージョン）で
  差分の有無をまず数のレベルでチェックすると効率的。
