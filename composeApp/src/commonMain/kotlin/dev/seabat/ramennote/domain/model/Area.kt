package dev.seabat.ramennote.domain.model

import kotlinx.datetime.LocalDate

data class Area(
    val name: String,
    val updatedDate: LocalDate,
    val count: Int
)
