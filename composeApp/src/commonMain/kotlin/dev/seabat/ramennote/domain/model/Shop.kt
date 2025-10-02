package dev.seabat.ramennote.domain.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class Shop(
    val name: String = "",
    val area: String = "",
    val shopUrl: String = "",
    val mapUrl: String = "",
    val star: Int = 0,
    val stationName: String = "",
    val category: String = "",
    val menuName1: String = "",
    val menuName2: String = "",
    val menuName3: String = "",
    val photoName1: String = "",
    val photoName2: String = "",
    val photoName3: String = "",
    val description1: String = "",
    val description2: String = "",
    val description3: String = ""
) {
    fun toJsonString(): String {
        return Json.encodeToString(this)
    }
    
    companion object {
        fun fromJsonString(jsonString: String): Shop {
            return Json.decodeFromString(jsonString)
        }
    }
}
