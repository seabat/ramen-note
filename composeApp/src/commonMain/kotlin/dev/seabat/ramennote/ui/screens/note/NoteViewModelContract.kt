package dev.seabat.ramennote.ui.screens.note

import dev.seabat.ramennote.domain.model.RunStatus
import kotlinx.coroutines.flow.StateFlow

interface NoteViewModelContract {
    val areas: StateFlow<List<AreaWithImage>>
    val imagesState: StateFlow<RunStatus<List<ByteArray>>>

    fun fetchAreas()
}
