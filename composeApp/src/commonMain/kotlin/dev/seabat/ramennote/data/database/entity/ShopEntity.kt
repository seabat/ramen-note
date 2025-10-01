package dev.seabat.ramennote.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shops")
data class ShopEntity(
    @PrimaryKey
    val name: String,
    val area: String,
    val shopUrl: String,
    val mapUrl: String,
    val star: Int,
    val stationName: String,
    val imageName1: String,
    val imageName2: String,
    val imageName3: String,
)
