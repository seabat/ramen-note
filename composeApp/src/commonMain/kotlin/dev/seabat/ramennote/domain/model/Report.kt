package dev.seabat.ramennote.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.json.Json

data class Report(
    val id: Int = 0,
    val shopId: Int = 0,
    val menuName: String = "",
    val photoName: String = "",
    val impression: String = "",
    val date: LocalDate? = null,
    val star: Int = 0
) {
    fun toJsonString(): String = Json.encodeToString(this)

    companion object {
        fun fromJsonString(jsonString: String): Shop = Json.decodeFromString(jsonString)
    }
}
