# UI/UX デザイナー エージェント メモリ

## プロジェクト概要
- ラーメン店管理アプリ「ramen-note」（KMP / Compose Multiplatform）
- Material Design 3 ベース、ウォームトーン（テラコッタ系）のカラースキーム
- 詳細は `patterns.md` を参照

## 重要ファイルパス
- テーマ: `ui/theme/Color.kt`, `ui/theme/Theme.kt`, `ui/theme/Type.kt`
- 共通コンポーネント: `ui/components/`（AppBar, AppProgressBar, button/, alert/, banner/, chart/）
- 画面固有の共通部品: `ui/screens/componens/`（スペルミスに注意: "components" ではなく "componens"）
- 画面: `ui/screens/home/`, `note/`, `history/`, `schedule/`, `settings/`

## 確認済みの主要な問題点（2026-03-14 初回レビュー）
詳細は `review-2026-03-14.md` を参照。
- Theme.kt が `mediumContrastLightColorScheme` / `mediumContrastDarkColorScheme` を使用（標準スキームでない）
- Typography は `bodyLarge` のみカスタム定義、他はデフォルト任せ
- `AppBar` が戻るボタン非表示時も透明アイコンで右側スペーサーを置く実装（非標準）
- `ShopInputField` が `OutlinedTextField` でなく `BasicTextField` + 手動ボーダーを使用
- `ActionButton` が Material3 の `Button` / `OutlinedButton` でなく `Box + clickable` の自作実装
- `ScheduleRow` のアイコンタッチターゲットが 24dp（最低 48dp 未満）
- `ShopScreen` の URL テキストリンク色が `Color.Blue`（ハードコード）
- HistoryScreen と NoteScreen の空状態表示がシンプルなテキスト1行のみ
- DatePickerDialog の OK/Cancel ボタンが英語ハードコード
- `collectAsState()` が一部で使用（`collectAsStateWithLifecycle()` 推奨）

## ユーザー設定・好み
- 提案のみ → 承認後に実装という進め方を希望（初回セッションで確認）
