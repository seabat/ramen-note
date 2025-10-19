package dev.seabat.ramennote.domain.model

import dev.seabat.ramennote.domain.util.createTodayLocalDate
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class FullReport(
    val id: Int = 0,
    val shopId: Int = 0,
    val shopName: String = "",
    val menuName: String = "",
    val photoName: String = "",
    val impression: String = "",
    val date: LocalDate = createTodayLocalDate(),
    val imageBytes: ByteArray? = null,
    val star: Int = 0
)
