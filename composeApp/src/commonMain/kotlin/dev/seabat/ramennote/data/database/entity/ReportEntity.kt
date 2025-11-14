package dev.seabat.ramennote.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reports")
data class ReportEntity(
    @PrimaryKey
    val id: Int,
    val shopId: Int,
    val menuName: String,
    val photoName: String,
    val impression: String,
    val date: String, // ISO8601 format string (yyyy-MM-dd)
    val star: Int
)
