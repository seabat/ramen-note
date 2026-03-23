# Firebase API セキュリティ設定

## 概要

`google-services.json` をパブリックリポジトリで管理するにあたり、
Google Cloud Console および Firebase Console にて以下のセキュリティ設定を実施済み。

---

## Firebase App Check

アプリの正当性を検証し、不正なクライアントからの API アクセスを防ぐ。

### 背景

`GenerativeBackend.googleAI()` 経由のリクエストは Firebase サーバー経由でプロキシされるため、
GCP の API キーに Android/iOS アプリ制限を設定しても効果がない（リクエスト元が Firebase サーバーになるため）。
そのため Firebase App Check によるアクセス保護に切り替えた。

### プロバイダー設定

| プラットフォーム | 本番ビルド | デバッグビルド |
|----------------|-----------|--------------|
| Android | Play Integrity | Debug プロバイダー |
| iOS | DeviceCheck | Debug プロバイダー |

### 実装

- **Android**: `RamenNoteApplication.onCreate()` で `BuildConfig.DEBUG` により切り替え
- **iOS**: `iOSApp.init()` で `#if DEBUG` により切り替え（`FirebaseApp.configure()` より前に設定）

### Firebase Console での設定

Firebase Console → App Check → APIs にて以下の API の適用を有効化：

- Firebase AI Logic（Gemini へのアクセスを App Check で保護）

---

## API キーのアプリケーション制限

### Android API キー

| 項目 | 設定値 |
|------|--------|
| 制限の種類 | Android アプリ |
| パッケージ名 | `dev.seabat.ramennote` |
| SHA-1 証明書フィンガープリント | デバッグ用(~/.android/debug.keystore から取得)・リリース用(Play Console に登録しているアプリ署名鍵の証明書から取得)を登録済み |
| API の制限 | Generative Language API、Firebase App Check API を許可 |

### iOS API キー

| 項目 | 設定値 |
|------|--------|
| 制限の種類 | iOS アプリ |
| Bundle ID | `dev.seabat.ramennote` |
| API の制限 | Generative Language API、Firebase App Check API を許可 |

> **備考**: App Check のデバッグトークン交換（`firebaseappcheck.googleapis.com`）が
> Android/iOS API キー経由で行われるため、各キーの許可リストに `Firebase App Check API` を追加済み。

---

## Gemini Developer API キー（Firebase が自動生成）

| 項目 | 設定値 |
|------|--------|
| API の制限 | 制限なし（デフォルト） |
| アプリケーションの制限 | 設定なし |

> **備考**: アプリは `GenerativeBackend.googleAI()` を使用しており、リクエストは Firebase サーバー経由でプロキシされる。
>
> ```
> アプリ
>   ↓（Firebase API キー + App Check トークン）
> Firebase サーバー
>   ↓（Gemini Developer API キー）← Firebase が使用
> Gemini API（generativelanguage.googleapis.com）
> ```
>
> アプリがこのキーを直接使うわけではないが、Firebase が Gemini を呼び出す際にサーバー側で使用する。
> Firebase がプロジェクト設定時に自動生成したキーであり、アプリ側での管理は不要。
> エンドユーザーからのアクセス保護は Firebase App Check（Android: Play Integrity、iOS: DeviceCheck）が担う。

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

- 2026-03-22: API キー制限・クォータ設定
- 2026-03-23: Firebase App Check 導入
