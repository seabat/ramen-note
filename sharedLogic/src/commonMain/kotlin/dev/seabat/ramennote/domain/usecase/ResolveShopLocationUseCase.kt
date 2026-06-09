package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.GeocodingRepositoryContract
import dev.seabat.ramennote.data.repository.ShopsRepositoryContract
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.model.Shop
import dev.seabat.ramennote.domain.model.ShopLocation
import io.ktor.http.decodeURLQueryComponent

/**
 * ショップの mapUrl を解析して [ShopLocation] を返す UseCase。
 *
 * mapUrl の形式によって以下の3通りの処理を行う。
 * - 座標付き URL（`/maps?q=lat,lng&z=16`）: URL から座標を直接パースして返す
 * - 旧座標付き URL（`?q=lat,lng`）: 座標をパースし /maps?q= 形式で DB を上書きしてから返す
 * - クエリ形式（`query=...`）: Geocoding API で座標を取得し mapUrl を DB に上書きしてから返す
 *
 * mapUrl が空または上記のいずれにも該当しない場合は `null` を返す。
 */
class ResolveShopLocationUseCase(
    private val geocodingRepository: GeocodingRepositoryContract,
    private val shopsRepository: ShopsRepositoryContract
) : ResolveShopLocationUseCaseContract {
    override suspend operator fun invoke(shop: Shop): ShopLocation? {
        if (shop.mapUrl.isEmpty()) return null

        return when {
            // 座標付き URL（例: https://maps.google.com/maps?q=35.689,139.691&z=16）
            shop.mapUrl.contains("maps.google.com/maps?q=") -> parseCoordinatesFromMapsQUrl(shop)
            // 旧座標付き URL（例: https://maps.google.com/?q=35.689,139.691）→ /maps?q= 形式へ移行
            shop.mapUrl.contains("?q=") -> parseCoordinatesFromQUrl(shop)
            // query= 形式（例: https://www.google.com/maps/search/?api=1&query=aiya+tokushima）
            shop.mapUrl.contains("query=") -> geocodeAndUpdate(shop)
            else -> null
        }
    }

    /**
     * `/maps?q=lat,lng&z=16` 形式の URL から座標を取り出す。パース失敗時は `null` を返す。
     */
    private suspend fun parseCoordinatesFromMapsQUrl(shop: Shop): ShopLocation? =
        try {
            val qParam = shop.mapUrl.substringAfter("maps.google.com/maps?q=").substringBefore("&")
            val parts = qParam.split(",")
            val lat = parts[0].toDouble()
            val lng = parts[1].toDouble()
            // 日本の座標範囲外はジオコーディング誤キャッシュとみなして店舗名で再ジオコーディング
            if (lat !in 24.0..46.0 || lng !in 122.0..154.0) return geocodeByShopName(shop)
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
            val newMapUrl = "https://maps.google.com/maps?q=$lat,$lng&z=16"
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
                val (lat, lng) = result.data!!
                val newMapUrl = "https://maps.google.com/maps?q=$lat,$lng&z=16"
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
                val (lat, lng) = result.data!!
                val newMapUrl = "https://maps.google.com/maps?q=$lat,$lng&z=16"
                shopsRepository.updateMapUrl(shop.id, newMapUrl)
                ShopLocation(shop, lat, lng)
            }
            else -> null
        }
    }
}
