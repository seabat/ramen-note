package dev.seabat.ramennote.ui.screens.note.addarea

import dev.seabat.ramennote.domain.model.RunStatus
import kotlinx.coroutines.flow.StateFlow

interface AddAreaViewModelContract {
    fun addArea(area: String)
    val addState: StateFlow<RunStatus<ByteArray?>>
}
