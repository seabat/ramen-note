package dev.seabat.ramennote.ui.screens.note.editareasort

import androidx.lifecycle.ViewModel
import dev.seabat.ramennote.domain.model.Area
import dev.seabat.ramennote.domain.model.RunStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.LocalDate

/**
 * Preview用のモックEditAreaSortViewModel
 * 実際のデータベースアクセスを行わず、固定のデータを返す
 */
class MockEditAreaSortViewModel :
    ViewModel(),
    EditAreaSortViewModelContract {
    override val areas: StateFlow<List<Area>> =
        MutableStateFlow(
            listOf(
                Area(name = "東京", updatedDate = LocalDate(2024, 9, 1), count = 12, sort = 3),
                Area(name = "神奈川", updatedDate = LocalDate(2024, 8, 21), count = 5, sort = 2),
                Area(name = "徳島", updatedDate = LocalDate(2024, 7, 3), count = 2, sort = 1),
                Area(name = "愛媛", updatedDate = LocalDate(2024, 6, 14), count = 7, sort = 4)
            )
        ).asStateFlow()

    override val editAreasState: StateFlow<RunStatus<String>> =
        MutableStateFlow(RunStatus.Idle<String>()).asStateFlow()

    override fun loadAreas() {
        // Preview用なので何もしない
    }

    override fun setSort(areaName: String, sort: Int) {
        // Preview用なので何もしない
    }

    override fun editAreaSort() {
        // Preview用なので何もしない
    }
}
