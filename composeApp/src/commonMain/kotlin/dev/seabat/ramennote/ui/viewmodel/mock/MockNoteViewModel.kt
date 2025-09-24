package dev.seabat.ramennote.ui.viewmodel.mock

import androidx.lifecycle.ViewModel
import dev.seabat.ramennote.domain.model.Area
import dev.seabat.ramennote.ui.viewmodel.NoteViewModelContract
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.LocalDate

/**
 * Preview用のモックViewModel
 * 実際のデータベースアクセスを行わず、固定のデータを返す
 */
class MockNoteViewModel : ViewModel(), NoteViewModelContract {
    private val _areas: MutableStateFlow<List<Area>> = MutableStateFlow(
        listOf(
            Area(name = "東京", updatedDate = LocalDate(2024, 9, 1), count = 12),
            Area(name = "神奈川", updatedDate = LocalDate(2024, 8, 21), count = 5),
            Area(name = "徳島", updatedDate = LocalDate(2024, 7, 3), count = 2),
            Area(name = "愛媛", updatedDate = LocalDate(2024, 6, 14), count = 7)
        )
    )
    override val areas: StateFlow<List<Area>> = _areas.asStateFlow()

    override fun fetchAreas() {
        // Preview用なので何もしない
    }
}