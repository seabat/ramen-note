package dev.seabat.ramennote.data.repository

import dev.seabat.ramennote.domain.model.Area
import dev.seabat.ramennote.domain.model.RunStatus
import kotlinx.coroutines.delay
import kotlinx.datetime.LocalDate

class AreasRepository {
    companion object {
        // 仮のスタブデータ
        private val _areas: MutableList<Area> = mutableListOf(
            Area(name = "東京", updatedDate = LocalDate(2024, 9, 1), count = 12),
            Area(name = "神奈川", updatedDate = LocalDate(2024, 8, 21), count = 5),
            Area(name = "徳島", updatedDate = LocalDate(2024, 7, 3), count = 2),
            Area(name = "愛媛", updatedDate = LocalDate(2024, 6, 14), count = 7)
        )
    }

    suspend fun fetch(): List<Area> {
        delay(300)
        return _areas.toList()
    }

    suspend fun add(area: Area) {
        delay(200)
        _areas.add(0, area)
    }

    suspend fun edit(oldName: String, newName: String): RunStatus<String> {
        delay(200)
        val index = _areas.indexOfFirst { it.name == oldName }
        if (index != -1) {
            val existingArea = _areas[index]
            _areas[index] = existingArea.copy(name = newName)
            return RunStatus.Success(data = "")
        }
        return RunStatus.Error(errorMessage = "${oldName}は登録されていません。編集に失敗しました")
    }

    suspend fun delete(areaName: String): RunStatus<String> {
        delay(200)
        val index = _areas.indexOfFirst { it.name == areaName }
        if (index != -1) {
            _areas.removeAt(index)
            return RunStatus.Success(data = "")
        }
        return RunStatus.Error(errorMessage = "${areaName}は登録されていません。削除に失敗しました")
    }
}