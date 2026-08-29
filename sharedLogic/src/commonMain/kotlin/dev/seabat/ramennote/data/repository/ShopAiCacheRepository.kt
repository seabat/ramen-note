package dev.seabat.ramennote.data.repository

import dev.seabat.ramennote.data.database.dao.ShopAiCacheDao
import dev.seabat.ramennote.data.database.entity.ShopAiCacheEntity
import dev.seabat.ramennote.domain.model.ShopAiInfo
import dev.seabat.ramennote.domain.util.createTodayLocalDate

class ShopAiCacheRepository(
    private val shopAiCacheDao: ShopAiCacheDao
) : ShopAiCacheRepositoryContract {
    override suspend fun get(areaName: String, shopName: String): ShopAiInfo? =
        shopAiCacheDao.getByAreaAndShop(areaName, shopName)?.let { entity ->
            ShopAiInfo(
                shopName = entity.shopName,
                shopUrl = entity.shopUrl,
                mapUrl = entity.mapUrl,
                stationName = entity.stationName,
                category = entity.category,
                description = entity.description
            )
        }

    override suspend fun save(areaName: String, shopName: String, info: ShopAiInfo) {
        shopAiCacheDao.insert(
            ShopAiCacheEntity(
                areaName = areaName,
                shopName = shopName,
                shopUrl = info.shopUrl,
                mapUrl = info.mapUrl,
                stationName = info.stationName,
                category = info.category,
                description = info.description,
                createdAt = createTodayLocalDate().toString()
            )
        )
    }
}
