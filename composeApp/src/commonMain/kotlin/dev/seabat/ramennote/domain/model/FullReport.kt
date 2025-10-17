package dev.seabat.ramennote.domain.model

import kotlinx.datetime.LocalDate

data class FullReport(
    val id: Int,
    val shopName: String,
    val menuName: String,
    val photoName: String,
    val impression: String,
    val date: LocalDate,
    val imageBytes: ByteArray? = null
)
