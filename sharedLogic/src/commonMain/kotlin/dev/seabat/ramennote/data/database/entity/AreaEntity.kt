package dev.seabat.ramennote.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * AreaEntity
 *
 * @property areaId 自動採番の主キー
 * @property name エリア名（一意インデックス）
 * @property count
 * @property date ISO8601 format string
 * @property sort UI上の順序を意味するカラム
 */
@Entity(
    tableName = "areas",
    indices = [Index(value = ["name"], unique = true)]
)
data class AreaEntity(
    @PrimaryKey(autoGenerate = true) val areaId: Int = 0,
    val name: String,
    val count: Int,
    val date: String,
    val sort: Int
)
