package dev.seabat.ramennote.data.repository

import dev.seabat.ramennote.data.database.RamenNoteDatabase
import dev.seabat.ramennote.data.database.dao.ShopDao
import dev.seabat.ramennote.data.database.entity.ShopEntity
import dev.seabat.ramennote.domain.model.Shop
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ShopsRepository(
    private val database: RamenNoteDatabase
) : ShopsRepositoryContract {

    private val shopDao: ShopDao by lazy {
        database.shopDao()
    }

    override suspend fun getAllShops(): List<Shop> {
        return shopDao.getAllShops().map { it.toDomainModel() }
    }

    override fun getAllShopsFlow(): Flow<List<Shop>> {
        return shopDao.getAllShopsFlow().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun getShopByName(name: String): Shop? {
        return shopDao.getShopByName(name)?.toDomainModel()
    }

    override suspend fun getShopsByArea(area: String): List<Shop> {
        return shopDao.getShopsByArea(area).map { it.toDomainModel() }
    }

    override suspend fun insertShop(shop: Shop) {
        shopDao.insertShop(shop.toEntity())
    }

    override suspend fun updateShop(shop: Shop) {
        shopDao.updateShop(shop.toEntity())
    }

    override suspend fun deleteShop(shop: Shop) {
        shopDao.deleteShop(shop.toEntity())
    }

    override suspend fun deleteShopByName(name: String) {
        shopDao.deleteShopByName(name)
    }
}

private fun ShopEntity.toDomainModel(): Shop {
    return Shop(
        name = name,
        area = area,
        shopUrl = shopUrl,
        mapUrl = mapUrl,
        star = star,
        stationName = stationName,
        category = "" // ShopEntityにはcategoryがないので空文字を設定
    )
}

private fun Shop.toEntity(): ShopEntity {
    return ShopEntity(
        name = name,
        area = area,
        shopUrl = shopUrl,
        mapUrl = mapUrl,
        star = star,
        stationName = stationName,
        imageName1 = "", // 画像名は空文字で初期化
        imageName2 = "",
        imageName3 = ""
    )
}
