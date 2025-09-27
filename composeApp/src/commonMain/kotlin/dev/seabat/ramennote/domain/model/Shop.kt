package dev.seabat.ramennote.domain.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class Shop(
    val name: String,
    val area: String,
    val shopUrl: String,
    val mapUrl: String,
    val star: Int,
    val stationName: String,
    val category: String
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
