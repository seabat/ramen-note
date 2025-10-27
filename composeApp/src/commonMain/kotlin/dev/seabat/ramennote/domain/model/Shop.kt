package dev.seabat.ramennote.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Shop(
    val id: Int = 0,
    val name: String = "",
    val area: String = "",
    val shopUrl: String = "",
    val mapUrl: String = "",
    val star: Int = 0,
    val stationName: String = "",
    val category: String = "",
    val scheduledDate: LocalDate? = null,
    val menuName1: String = "",
    val menuName2: String = "",
    val menuName3: String = "",
    val photoName1: String = "",
    val photoName2: String = "",
    val photoName3: String = "",
    val description1: String = "",
    val description2: String = "",
    val description3: String = "",
    val favorite: Boolean = false
) {
    fun toJsonString(): String = Json.encodeToString(this)

    companion object {
        fun fromJsonString(jsonString: String): Shop = Json.decodeFromString(jsonString)
    }
}
