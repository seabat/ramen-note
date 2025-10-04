package dev.seabat.ramennote.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shops")
data class ShopEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val area: String,
    val shopUrl: String,
    val mapUrl: String,
    val star: Int,
    val stationName: String,
    val category: String,
    val menuName1: String,
    val menuName2: String,
    val menuName3: String,
    val photoName1: String,
    val photoName2: String,
    val photoName3: String,
    val description1: String,
    val description2: String,
    val description3: String
)
