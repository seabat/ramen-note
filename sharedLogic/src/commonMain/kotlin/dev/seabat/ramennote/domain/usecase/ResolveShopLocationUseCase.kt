package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.ExpandShortUrlRepositoryContract
import dev.seabat.ramennote.data.repository.GeocodingRepositoryContract
import dev.seabat.ramennote.data.repository.ShopsRepositoryContract
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.model.Shop
import dev.seabat.ramennote.domain.model.ShopLocation
import io.ktor.http.decodeURLQueryComponent
import io.ktor.http.encodeURLQueryComponent

/**
 * ショップの mapUrl を解析して [ShopLocation] を返す UseCase。
 *
 * mapUrl の形式によって以下の4通りの処理を行う。
 * - 座標付き URL（`/maps?q=name@lat,lng&z=16`）: URL から座標を直接パースして返す
 * - 旧座標付き URL（`?q=lat,lng`）: 座標をパースし /maps?q= 形式で DB を上書きしてから返す
 * - クエリ形式（`query=...`）: Geocoding API で座標を取得し mapUrl を DB に上書きしてから返す
 * - 短縮 URL（`maps.app.goo.gl`）: リダイレクト展開して座標を取得し DB を上書きしてから返す
 *
 * mapUrl が空または上記のいずれにも該当しない場合は `null` を返す。
 */
class ResolveShopLocationUseCase(
    private val geocodingRepository: GeocodingRepositoryContract,
    private val shopsRepository: ShopsRepositoryContract,
    private val expandShortUrlRepository: ExpandShortUrlRepositoryContract
) : ResolveShopLocationUseCaseContract {
    override suspend operator fun invoke(shop: Shop): ShopLocation? {
        if (shop.mapUrl.isEmpty()) return null

        return when {
            // 座標付き URL（例: https://maps.google.com/maps?q=店舗名@35.689,139.691&z=16）
            shop.mapUrl.contains("maps.google.com/maps?q=") -> parseCoordinatesFromMapsQUrl(shop)
            // 旧座標付き URL（例: https://maps.google.com/?q=35.689,139.691）→ /maps?q= 形式へ移行
            shop.mapUrl.contains("?q=") -> parseCoordinatesFromQUrl(shop)
            // query= 形式（例: https://www.google.com/maps/search/?api=1&query=aiya+tokushima）
            shop.mapUrl.contains("query=") -> geocodeAndUpdate(shop)
            // Google Maps 短縮 URL（例: https://maps.app.goo.gl/Wjj4TJTp6mjzLyTH8）
            shop.mapUrl.contains("maps.app.goo.gl") -> expandAndResolve(shop)
            else -> null
        }
    }

    /**
     * `/maps?q=name@lat,lng&z=16` 形式の URL から座標を取り出す。
     * `name@lat,lng` と `lat,lng`（店舗名なし）の両形式に対応する。パース失敗時は `null` を返す。
     */
    private suspend fun parseCoordinatesFromMapsQUrl(shop: Shop): ShopLocation? =
        try {
            val qParam = shop.mapUrl.substringAfter("maps.google.com/maps?q=").substringBefore("&")
            // name@lat,lng 形式と lat,lng 形式の両方に対応
            val coordsStr = if (qParam.contains("@")) qParam.substringAfterLast("@") else qParam
            val parts = coordsStr.split(",")
            val lat = parts[0].toDouble()
            val lng = parts[1].toDouble()
            // 日本の座標範囲外はジオコーディング誤キャッシュとみなして店舗名で再ジオコーディング
            if (lat !in 24.0..46.0 || lng !in 122.0..154.0) return geocodeByShopName(shop)
            // 店舗名なし（旧形式）は店舗名付き URL に更新する
            if (!qParam.contains("@")) {
                shopsRepository.updateMapUrl(shop.id, buildMapUrl(shop, lat, lng))
            }
            ShopLocation(shop, lat, lng)
        } catch (_: Exception) {
            null
        }

    /**
     * 旧形式 `?q=lat,lng` の URL から座標をパースし、/maps?q= 形式で DB を上書きする。
     */
    private suspend fun parseCoordinatesFromQUrl(shop: Shop): ShopLocation? =
        try {
            val qParam = shop.mapUrl.substringAfter("?q=").substringBefore("&")
            val parts = qParam.split(",")
            val lat = parts[0].toDouble()
            val lng = parts[1].toDouble()
            if (lat !in 24.0..46.0 || lng !in 122.0..154.0) return geocodeByShopName(shop)
            val newMapUrl = buildMapUrl(shop, lat, lng)
            shopsRepository.updateMapUrl(shop.id, newMapUrl)
            ShopLocation(shop, lat, lng)
        } catch (_: Exception) {
            null
        }

    /**
     * `query=` 形式の URL から店舗名クエリを取り出し Geocoding API で座標を解決する。
     * 成功時は mapUrl を座標付き URL で DB に上書きする。失敗時は `null` を返す。
     */
    private suspend fun geocodeByShopName(shop: Shop): ShopLocation? {
        val query = buildString {
            append(shop.name)
            if (shop.stationName.isNotEmpty()) append(" ${shop.stationName}駅")
        }
        return when (val result = geocodingRepository.geocode(query)) {
            is RunStatus.Success -> {
                val (lat, lng) = requireNotNull(result.data) { "geocode result data is null" }
                val newMapUrl = buildMapUrl(shop, lat, lng)
                shopsRepository.updateMapUrl(shop.id, newMapUrl)
                ShopLocation(shop, lat, lng)
            }
            else -> null
        }
    }

    private suspend fun geocodeAndUpdate(shop: Shop): ShopLocation? {
        // `+` はスペースの URL エンコード。decodeURLQueryComponent で正しく復元してから API に渡す。
        val query = shop.mapUrl.substringAfter("query=").substringBefore("&").decodeURLQueryComponent()
        return when (val result = geocodingRepository.geocode(query)) {
            is RunStatus.Success -> {
                val (lat, lng) = requireNotNull(result.data) { "geocode result data is null" }
                val newMapUrl = buildMapUrl(shop, lat, lng)
                shopsRepository.updateMapUrl(shop.id, newMapUrl)
                ShopLocation(shop, lat, lng)
            }
            else -> null
        }
    }

    /**
     * `maps.app.goo.gl` 短縮 URL をリダイレクト展開して座標を取得する。
     *
     * 展開先 URL を以下の優先順で処理する:
     * 1. `/@lat,lng,zoom` 形式 → 直接パース
     * 2. `?q=住所` 形式 → 住所で Geocoding API（店舗名より精度が高い）
     * 3. 全て失敗 → 店舗名で Geocoding API にフォールバック
     */
    private suspend fun expandAndResolve(shop: Shop): ShopLocation? {
        val expandResult = expandShortUrlRepository.expand(shop.mapUrl)
        if (expandResult !is RunStatus.Success) return geocodeByShopName(shop)
        val expandedUrl = requireNotNull(expandResult.data) { "expanded URL is null" }

        // 1. /@lat,lng 形式を試みる
        if (expandedUrl.contains("/@")) {
            try {
                val afterAt = expandedUrl.substringAfter("/@").substringBefore("/")
                val parts = afterAt.split(",")
                val lat = parts[0].toDouble()
                val lng = parts[1].toDouble()
                if (lat in 24.0..46.0 && lng in 122.0..154.0) {
                    val newMapUrl = buildMapUrl(shop, lat, lng)
                    shopsRepository.updateMapUrl(shop.id, newMapUrl)
                    return ShopLocation(shop, lat, lng)
                }
            } catch (_: Exception) { /* 次の手段へ */ }
        }

        // 2. q= パラメータの住所でジオコーディング
        val address = try {
            expandedUrl.substringAfter("?q=").substringBefore("&").decodeURLQueryComponent()
        } catch (_: Exception) { null }

        if (!address.isNullOrEmpty()) {
            when (val geocodeResult = geocodingRepository.geocode(address)) {
                is RunStatus.Success -> {
                    val (lat, lng) = requireNotNull(geocodeResult.data) { "geocode result data is null" }
                    if (lat in 24.0..46.0 && lng in 122.0..154.0) {
                        val newMapUrl = buildMapUrl(shop, lat, lng)
                        shopsRepository.updateMapUrl(shop.id, newMapUrl)
                        return ShopLocation(shop, lat, lng)
                    }
                }
                else -> { /* 次の手段へ */ }
            }
        }

        // 3. 店舗名でジオコーディング
        return geocodeByShopName(shop)
    }

    // ピンに店舗名を表示するため q=name@lat,lng 形式を使用する
    private fun buildMapUrl(shop: Shop, lat: Double, lng: Double): String {
        val encodedName = shop.name.encodeURLQueryComponent()
        return "https://maps.google.com/maps?q=$encodedName@$lat,$lng&z=16"
    }
}
