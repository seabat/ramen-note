package dev.seabat.ramennote.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.seabat.ramennote.data.database.entity.ShopAiCacheEntity

@Dao
interface ShopAiCacheDao {
    @Query("SELECT * FROM shop_ai_cache WHERE areaName = :areaName AND shopName = :shopName")
    suspend fun getByAreaAndShop(areaName: String, shopName: String): ShopAiCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ShopAiCacheEntity)
}
