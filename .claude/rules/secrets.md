# 機密情報（API キー等）の管理

- `local.properties` に機密値を定義（`.gitignore` 済み）
- `sharedLogic/build.gradle.kts` の `generateBuildSecrets` タスクが `BuildSecrets.kt` を自動生成
- commonMain から `BuildSecrets.UNSPLASH_ACCESS_KEY` などで参照
- `local.properties`・`google-services.json`・`.env` への Claude Code からの編集は Hooks によりブロックされる
