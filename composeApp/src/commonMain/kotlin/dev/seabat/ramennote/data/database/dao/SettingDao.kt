package dev.seabat.ramennote.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import dev.seabat.ramennote.data.database.entity.SettingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingDao {
    @Query("SELECT * FROM settings")
    suspend fun getAllSettings(): List<SettingEntity>

    @Query("SELECT * FROM settings")
    fun getAllSettingsFlow(): Flow<List<SettingEntity>>

    @Query("SELECT * FROM settings WHERE key = :key")
    suspend fun getSettingByKey(key: String): SettingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetting(setting: SettingEntity)

    @Update
    suspend fun updateSetting(setting: SettingEntity)

    @Delete
    suspend fun deleteSetting(setting: SettingEntity)

    @Query("DELETE FROM settings WHERE key = :key")
    suspend fun deleteSettingByKey(key: String)
}

