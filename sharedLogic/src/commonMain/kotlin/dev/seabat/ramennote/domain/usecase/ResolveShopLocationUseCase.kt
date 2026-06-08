package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.GeocodingRepositoryContract
import dev.seabat.ramennote.data.repository.ShopsRepositoryContract
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.model.Shop
import dev.seabat.ramennote.domain.model.ShopLocation

/**
 * ショップの mapUrl を解析して [ShopLocation] を返す UseCase。
 *
 * mapUrl の形式によって以下の2通りの処理を行う。
 * - 座標付き URL（`?q=lat,lng`）: URL から座標を直接パースして返す
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
            // 既に座標付き URL（例: https://maps.google.com/?q=35.689,139.691）
            shop.mapUrl.contains("?q=") -> parseCoordinatesFromUrl(shop)
            // query= 形式（例: https://www.google.com/maps/search/?api=1&query=aiya+tokushima）
            shop.mapUrl.contains("query=") -> geocodeAndUpdate(shop)
            else -> null
        }
    }

    /**
     * `?q=lat,lng` 形式の URL から座標を取り出す。パース失敗時は `null` を返す。
     */
    private fun parseCoordinatesFromUrl(shop: Shop): ShopLocation? =
        try {
            val qParam = shop.mapUrl.substringAfter("?q=").substringBefore("&")
            val parts = qParam.split(",")
            val lat = parts[0].toDouble()
            val lng = parts[1].toDouble()
            ShopLocation(shop, lat, lng)
        } catch (_: Exception) {
            null
        }

    /**
     * `query=` 形式の URL から店舗名クエリを取り出し Geocoding API で座標を解決する。
     * 成功時は mapUrl を座標付き URL で DB に上書きする。失敗時は `null` を返す。
     */
    private suspend fun geocodeAndUpdate(shop: Shop): ShopLocation? {
        val query = shop.mapUrl.substringAfter("query=").substringBefore("&")
        return when (val result = geocodingRepository.geocode(query)) {
            is RunStatus.Success -> {
                val (lat, lng) = result.data!!
                val newMapUrl = "https://maps.google.com/?q=$lat,$lng"
                shopsRepository.updateMapUrl(shop.id, newMapUrl)
                ShopLocation(shop, lat, lng)
            }
            else -> null
        }
    }
}
