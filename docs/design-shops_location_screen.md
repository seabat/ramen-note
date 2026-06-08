# ShopsLocationScreen 設計ドキュメント

## 概要

エリアに登録されているショップの位置を Google Map 上に表示する画面。
`NoteScreen` の `AreaItem` からマップボタンをタップして遷移する。

---

## 確定した設計決定

| # | 項目 | 決定 |
|---|---|---|
| 1 | マップ実装 | expect/actual（Android: maps-compose、iOS: UIKitView + MapKit） |
| 2 | 座標取得 | Google Geocoding API（Ktor、commonMain） |
| 3 | 座標キャッシュ | ShopsLocationScreen 表示時に `mapUrl` を座標付き URL で DB 更新 |
| 4 | Geocoding 失敗 | 該当ショップをスキップ（地図上に表示しない） |
| 5 | ナビゲーション引数 | `ShopsLocation(areaId: Int, areaName: String)` |
| 6 | ボタン配置 | `AreaItem` 右下、`ReportListButton` の隣 |
| 7 | ローディング | `AppProgressBar` オーバーレイ（全件解決後に消える） |
| 8 | iOS 実装 | iosMain Kotlin + `UIKitView` |
| 9 | API キー | Geocoding・Maps SDK 共通1キー（`GOOGLE_MAPS_API_KEY`） |
| 10 | AppBar タイトル | `areaName` をナビゲーション引数で渡す |

---

## mapUrl フォーマット

| 状態 | フォーマット例 |
|---|---|
| 未解決（DB 登録時） | `https://www.google.com/maps/search/?api=1&query=aiya+tokushima` |
| 解決済み（Geocoding 後） | `https://maps.google.com/?q=35.689,139.691` |

画面表示時に `query=` 形式の URL を検出した場合のみ Geocoding API を呼び出し、結果で `mapUrl` を上書き保存する。次回以降は API 呼び出しなしで座標を直接利用する。

---

## UX フロー

1. `NoteScreen` の `AreaItem` 右下のマップボタン（地図アイコン＋"マップ"テキスト）をタップ
2. `ShopsLocationScreen` に遷移（`areaId`・`areaName` を引数で渡す）
3. 地図を即時表示 ＋ `AppProgressBar` オーバーレイを表示
4. 全ショップの Geocoding を並列実行
   - 成功 → `mapUrl` を DB 更新、ピンを地図に追加
   - 失敗 → スキップ（地図上に表示しない）
5. 全件処理完了後に `AppProgressBar` を非表示
6. 地図の縮尺はすべてのピンがギリギリ収まるよう自動調整
7. ピンをタップ → `ShopScreen` に遷移

---

## 実装スコープ

### 新規ファイル（sharedLogic）

| ファイル | 役割 |
|---|---|
| `GeocodingRepositoryContract.kt` | Geocoding API のリポジトリ Contract |
| `GeocodingRepository.kt` | Geocoding API の HTTP 呼び出し（Ktor） |
| `LoadShopListByAreaIdUseCaseContract.kt` | areaId でショップ一覧取得の UseCase Contract |
| `LoadShopListByAreaIdUseCase.kt` | areaId でショップ一覧取得の実装 |
| `ResolveShopLocationUseCaseContract.kt` | query= URL → 座標変換 + mapUrl DB 更新の UseCase Contract |
| `ResolveShopLocationUseCase.kt` | 座標変換 + mapUrl DB 更新の実装 |

### 新規ファイル（sharedUI）

| ファイル | 役割 |
|---|---|
| `ShopsLocationViewModelContract.kt` | ViewModel Contract |
| `ShopsLocationViewModel.kt` | ViewModel 実装 |
| `MockShopsLocationViewModel.kt` | Preview 用 Mock |
| `ShopsLocationScreen.kt` | 画面 Composable |
| `ShopsMap.common.kt` | マップ Composable の expect 宣言 |
| `ShopsMap.android.kt` | maps-compose による Android 実装 |
| `ShopsMap.ios.kt` | UIKitView + MapKit による iOS 実装 |
| `ShopLocation.kt` | データクラス（`shop: Shop, lat: Double, lng: Double`） |

### 既存ファイルの変更

| ファイル | 変更内容 |
|---|---|
| `ShopDao.kt` | `updateMapUrl(shopId: Int, mapUrl: String)` 追加 |
| `ShopsRepositoryContract.kt` | `updateMapUrl` 追加 |
| `ShopsRepository.kt` | `updateMapUrl` 実装 |
| `NoteScreen.kt` | `AreaItem` と `NoteScreen` に `onMapClick` コールバック追加 |
| `MainNavigation.kt` | `Screen.ShopsLocation(areaId, areaName)` と composable ルート追加 |
| `DataModule.common.kt` | `GeocodingRepository` を `repositoryModule` に登録 |
| `DomainModule.kt` | 新 UseCase を登録 |
| `ViewModelModule.kt` | `ShopsLocationViewModel` を登録 |
| `local.properties` | `GOOGLE_MAPS_API_KEY=<キー>` 追加 |
| `BuildSecrets.kt`（自動生成） | `GOOGLE_MAPS_API_KEY` 追加 |
| `composeApp/build.gradle.kts` | `generateBuildSecrets` タスクに `GOOGLE_MAPS_API_KEY` 追加 |
| `sharedUI/build.gradle.kts` | `maps-compose` 依存追加 |
| `AndroidManifest.xml` | Maps SDK API キーの `meta-data` 追加 |

---

## Geocoding API

- エンドポイント: `https://maps.googleapis.com/maps/api/geocode/json`
- パラメータ: `address=<query>&key=<GOOGLE_MAPS_API_KEY>`
- 料金: 月間 10,000 件まで無料、以降 $5.00 / 1,000 件

### レスポンス（抜粋）

```json
{
  "results": [{
    "geometry": {
      "location": {
        "lat": 35.689,
        "lng": 139.691
      }
    }
  }],
  "status": "OK"
}
```

---

## マップ Composable インターフェース

```kotlin
// commonMain
data class ShopLocation(
    val shop: Shop,
    val latitude: Double,
    val longitude: Double
)

@Composable
expect fun ShopsMap(
    locations: List<ShopLocation>,
    onPinClick: (Shop) -> Unit,
    modifier: Modifier = Modifier
)
```

---

## ViewModel ステート

```kotlin
interface ShopsLocationViewModelContract {
    val locations: StateFlow<List<ShopLocation>>
    val isLoading: StateFlow<Boolean>

    fun loadShopsAndResolveLocations(areaId: Int)
}
```

---

## 残作業

### ユーザー対応（必須）

| # | 作業 | 詳細 |
|---|---|---|
| 1 | `GOOGLE_MAPS_API_KEY` を `local.properties` に追加 | `GOOGLE_MAPS_API_KEY=<キー>` を手動で記載。Claude Code からはフックにより編集不可。 |

### 将来対応（任意）

| # | 作業 | 詳細 |
|---|---|---|
| 2 | iOS でのピンタップ → ShopScreen 遷移 | Kotlin/Native の型バインディング制約により `MKMapViewDelegate` を Kotlin クラスで実装できなかった。Contract + Swift パターン（`SwiftLibDependencyFactoryContract` 経由）で Swift 側に委譲する方式への切り替えが必要。 |