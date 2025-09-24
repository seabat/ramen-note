package dev.seabat.ramennote.data.database.dao

import androidx.room.*
import dev.seabat.ramennote.data.database.entity.AreaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AreaDao {
    @Query("SELECT * FROM areas")
    suspend fun getAllAreas(): List<AreaEntity>

    @Query("SELECT * FROM areas")
    fun getAllAreasFlow(): Flow<List<AreaEntity>>

    @Query("SELECT * FROM areas WHERE name = :name")
    suspend fun getAreaByName(name: String): AreaEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArea(area: AreaEntity)

    @Update
    suspend fun updateArea(area: AreaEntity)

    @Delete
    suspend fun deleteArea(area: AreaEntity)

    @Query("DELETE FROM areas WHERE name = :name")
    suspend fun deleteAreaByName(name: String)
}
