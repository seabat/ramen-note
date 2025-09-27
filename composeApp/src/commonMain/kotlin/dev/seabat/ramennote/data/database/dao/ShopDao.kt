package dev.seabat.ramennote.data.database.dao

import androidx.room.*
import dev.seabat.ramennote.data.database.entity.ShopEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShopDao {
    @Query("SELECT * FROM shops")
    suspend fun getAllShops(): List<ShopEntity>

    @Query("SELECT * FROM shops")
    fun getAllShopsFlow(): Flow<List<ShopEntity>>

    @Query("SELECT * FROM shops WHERE name = :name")
    suspend fun getShopByName(name: String): ShopEntity?

    @Query("SELECT * FROM shops WHERE area = :area")
    suspend fun getShopsByArea(area: String): List<ShopEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShop(shop: ShopEntity)

    @Update
    suspend fun updateShop(shop: ShopEntity)

    @Delete
    suspend fun deleteShop(shop: ShopEntity)

    @Query("DELETE FROM shops WHERE name = :name")
    suspend fun deleteShopByName(name: String)
}
