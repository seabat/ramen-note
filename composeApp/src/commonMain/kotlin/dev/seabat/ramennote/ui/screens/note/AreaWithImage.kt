package dev.seabat.ramennote.ui.screens.note

import kotlinx.datetime.LocalDate

data class AreaWithImage(
    val name: String,
    val updatedDate: LocalDate,
    val count: Int,
    val imageBytes: ByteArray? = null
)
