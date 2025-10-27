package dev.seabat.ramennote.data.repository

import dev.seabat.ramennote.domain.model.Shop
import kotlinx.coroutines.flow.Flow

interface ShopsRepositoryContract {
    suspend fun getAllShops(): List<Shop>

    fun getAllShopsFlow(): Flow<List<Shop>>

    suspend fun getShopById(id: Int): Shop?

    suspend fun getShopsByArea(area: String): List<Shop>

    suspend fun insertShop(shop: Shop)

    suspend fun updateShop(shop: Shop)

    suspend fun deleteShop(shop: Shop)

    suspend fun deleteShopById(id: Int)
}
