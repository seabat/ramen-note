package dev.seabat.ramennote.data.repository

import dev.seabat.ramennote.domain.model.Area
import kotlinx.coroutines.delay
import kotlinx.datetime.LocalDate

class AreasRepository {
    // 仮のスタブデータ
    private val areas: List<Area> = listOf(
        Area(name = "東京", updatedDate = LocalDate(2024, 9, 1), count = 12),
        Area(name = "神奈川", updatedDate = LocalDate(2024, 8, 21), count = 5),
        Area(name = "徳島", updatedDate = LocalDate(2024, 7, 3), count = 2),
        Area(name = "愛媛", updatedDate = LocalDate(2024, 6, 14), count = 7)
    )

    suspend fun fetch(): List<Area> {
        delay(1000)
        return areas
    }
}