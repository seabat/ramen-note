package dev.seabat.ramennote.ui.screens.note.addarea

import dev.seabat.ramennote.domain.model.RunStatus
import kotlinx.coroutines.flow.StateFlow

interface AddAreaViewModelContract {
    fun addArea(area: String)
    fun fetchImage()
    val imageState: StateFlow<RunStatus<ByteArray?>>
}
