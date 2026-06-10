package dev.seabat.ramennote.domain.model

import kotlinx.datetime.LocalDate

/**
 * エリア
 *
 * @property name
 * @property updatedDate
 * @property count
 * @property sort UI上の順序を意味する。追加の際は Repository で自動採番する。
 */
data class Area(
    val areaId: Int = 0,
    val name: String,
    val updatedDate: LocalDate,
    val count: Int,
    val sort: Int
)
