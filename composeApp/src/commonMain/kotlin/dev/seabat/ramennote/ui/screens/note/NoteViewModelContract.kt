package dev.seabat.ramennote.ui.screens.note

import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.model.Shop
import kotlinx.coroutines.flow.StateFlow

interface NoteViewModelContract {
    val areas: StateFlow<List<AreaWithImage>>
    val shops: StateFlow<List<Shop>>
    val imagesState: StateFlow<RunStatus<List<ByteArray>>>

    fun fetchAreas()

    fun searchShops(query: String)
}
