package dev.seabat.ramennote.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * AreaEntity
 *
 * @property name
 * @property count
 * @property date ISO8601 format string
 */
@Entity(tableName = "areas")
data class AreaEntity(
    @PrimaryKey
    val name: String,
    val count: Int,
    val date: String
)
