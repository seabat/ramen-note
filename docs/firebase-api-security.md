# Firebase API セキュリティ設定

## 概要

`google-services.json` をパブリックリポジトリで管理するにあたり、
Google Cloud Console にて以下のセキュリティ設定を実施済み。

---

## API キーのアプリケーション制限

### Android API キー

| 項目 | 設定値                                                                                  |
|------|--------------------------------------------------------------------------------------|
| 制限の種類 | Android アプリ                                                                          |
| パッケージ名 | `dev.seabat.ramennote`                                                               |
| SHA-1 証明書フィンガープリント | デバッグ用(~/.android/debug.keystore から取得)・リリース用(Play Console に登録しているアプリ署名鍵の証明書から取得)を登録済み |

### iOS API キー

| 項目 | 設定値 |
|------|--------|
| 制限の種類 | iOS アプリ |
| Bundle ID | `dev.seabat.ramennote` |

---

## Gemini Developer API キー（Firebase が自動生成）

| 項目 | 設定値 |
|------|--------|
| API の制限 | Generative Language API のみ許可 |
| アプリケーションの制限 | 設定なし（Android/iOS キーで制御済みのため不要） |

> **備考**: アプリは `GenerativeBackend.googleAI()` を使用しており、
> Gemini Developer API（`generativelanguage.googleapis.com`）のグローバルエンドポイントを利用している。
> Vertex AI は使用していない。

---

## Gemini API 利用上限（クォータ）

Google Cloud Console → API とサービス → Generative Language API → 割り当て

| 指標 | リージョン | 設定値 |
|------|-----------|--------|
| Request limit per minute for a region | (default) | **200** |
| Request limit per minute for a region | リージョン指定 | 未設定 |

> **備考**: `(default)` はグローバルクォータに相当する。
> リージョン指定の項目は Vertex AI 向けのため、本アプリでは無関係。
> 500ユーザー想定。無料枠を考慮して 200 RPM に設定。

---

## 設定日

2026-03-22
