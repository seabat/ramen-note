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

    override suspend fun getShopById(id: Int): Shop? {
        return shopDao.getShopById(id)?.toDomainModel()
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

    override suspend fun deleteShopById(id: Int) {
        shopDao.deleteShopById(id)
    }
}

private fun ShopEntity.toDomainModel(): Shop {
    return Shop(
        id = id,
        name = name,
        area = area,
        shopUrl = shopUrl,
        mapUrl = mapUrl,
        star = star,
        stationName = stationName,
        category = category,
        menuName1 = menuName1,
        menuName2 = menuName2,
        menuName3 = menuName3,
        photoName1 = photoName1,
        photoName2 = photoName2,
        photoName3 = photoName3,
        description1 = description1,
        description2 = description2,
        description3 = description3
    )
}

private fun Shop.toEntity(): ShopEntity {
    return ShopEntity(
        id = id,
        name = name,
        area = area,
        shopUrl = shopUrl,
        mapUrl = mapUrl,
        star = star,
        stationName = stationName,
        category = category,
        menuName1 = menuName1,
        menuName2 = menuName2,
        menuName3 = menuName3,
        photoName1 = photoName1,
        photoName2 = photoName2,
        photoName3 = photoName3,
        description1 = description1,
        description2 = description2,
        description3 = description3
    )
}
