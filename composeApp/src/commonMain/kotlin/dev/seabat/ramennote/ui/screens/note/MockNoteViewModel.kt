package dev.seabat.ramennote.ui.screens.note

import dev.seabat.ramennote.domain.model.RunStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.LocalDate

/**
 * Preview用のモックViewModel
 * 実際のデータベースアクセスを行わず、固定のデータを返す
 */
class MockNoteViewModel : NoteViewModelContract {
    private val _areas: MutableStateFlow<List<AreaWithImage>> =
        MutableStateFlow(
            listOf(
                AreaWithImage(name = "東京", updatedDate = LocalDate(2024, 9, 1), count = 12),
                AreaWithImage(name = "神奈川", updatedDate = LocalDate(2024, 8, 21), count = 5),
                AreaWithImage(name = "徳島", updatedDate = LocalDate(2024, 7, 3), count = 2),
                AreaWithImage(name = "愛媛", updatedDate = LocalDate(2024, 6, 14), count = 7)
            )
        )
    override val areas: StateFlow<List<AreaWithImage>> = _areas.asStateFlow()

    private val _imagesState: MutableStateFlow<RunStatus<List<ByteArray>>> = MutableStateFlow(RunStatus.Idle())
    override val imagesState: StateFlow<RunStatus<List<ByteArray>>> = _imagesState.asStateFlow()

    override fun fetchAreas() {
        // Preview用なので何もしない
    }
}
