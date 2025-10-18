package dev.seabat.ramennote.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import dev.seabat.ramennote.data.database.entity.ReportEntity

@Dao
interface ReportDao {
    @Query("SELECT * FROM reports ORDER BY date ASC")
    suspend fun getAllReportsAsc(): List<ReportEntity>

    @Query("SELECT * FROM reports ORDER BY date DESC")
    suspend fun getAllReportsDesc(): List<ReportEntity>

    @Query("SELECT * FROM reports WHERE id = :id")
    suspend fun getReportById(id: Int): ReportEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(report: ReportEntity)

    @Update
    suspend fun update(report: ReportEntity)

    @Query("DELETE FROM reports WHERE id = :id")
    suspend fun deleteById(id: Int)
}


