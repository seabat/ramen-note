package dev.seabat.ramennote.ui.screens.note.editarea

import dev.seabat.ramennote.domain.model.RunStatus
import kotlinx.coroutines.flow.StateFlow

interface EditAreaViewModelContract {
    val deleteState: StateFlow<RunStatus<String>>
    val editState: StateFlow<RunStatus<String>>
    val imageState: StateFlow<RunStatus<ByteArray?>>
    var currentAreaName: String

    fun editArea(newArea: String)
    fun deleteArea(area: String)
    fun fetchImage(areaName: String)
    fun loadImage(name: String)
}