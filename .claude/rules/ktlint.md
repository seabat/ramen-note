# ktlint 設定

無効化ルール（`.editorconfig`）: `trailing-comma`、`function-signature`、`parameter-list-wrapping`、`expression-body-syntax`、`backing-property` / `property-naming`（`_xxx` StateFlow 許可）、`filename`（`logd.android.kt` 形式許可）、Composable 関数名の除外

ktlint 対象外: テストコード、ビルド出力、Gradle ファイル、secrets ディレクトリ

## 関連コマンド

```bash
./gradlew ktlintCheck                            # lint チェック
./gradlew ktlintFormat                           # lint 自動修正
```
