package dev.seabat.ramennote.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import dev.seabat.ramennote.data.database.entity.ShopEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShopDao {
    @Query("SELECT * FROM shops")
    suspend fun getAllShops(): List<ShopEntity>

    @Query("SELECT * FROM shops")
    fun getAllShopsFlow(): Flow<List<ShopEntity>>

    @Query("SELECT * FROM shops WHERE id = :id")
    suspend fun getShopById(id: Int): ShopEntity?

    @Query("SELECT * FROM shops WHERE area = :area")
    suspend fun getShopsByArea(area: String): List<ShopEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShop(shop: ShopEntity)

    @Update
    suspend fun updateShop(shop: ShopEntity)

    @Delete
    suspend fun deleteShop(shop: ShopEntity)

    @Query("DELETE FROM shops WHERE id = :id")
    suspend fun deleteShopById(id: Int)
}
