package dev.seabat.ramennote.ui.screens.note.editarea

import dev.seabat.ramennote.domain.model.RunStatus
import kotlinx.coroutines.flow.StateFlow

interface EditAreaViewModelContract {
    val deleteStatus: StateFlow<RunStatus<String>>
    val editStatus: StateFlow<RunStatus<String>>
    var currentAreas: String

    fun editArea(newArea: String)
    fun deleteArea(area: String)
}