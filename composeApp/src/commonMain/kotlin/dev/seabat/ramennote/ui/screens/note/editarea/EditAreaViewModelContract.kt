package dev.seabat.ramennote.ui.screens.note.editarea

import dev.seabat.ramennote.domain.model.RunStatus
import kotlinx.coroutines.flow.StateFlow

interface EditAreaViewModelContract {
    val deleteState: StateFlow<RunStatus<String>>
    val editState: StateFlow<RunStatus<String>>
    val imageState: StateFlow<RunStatus<ByteArray>>
    val areaName: StateFlow<String>

    fun editArea(areaId: Int, newAreaName: String)

    fun deleteArea(areaId: Int)

    fun fetchNewImage(areaId: Int)

    fun loadAreaName(areaId: Int)

    fun loadImage(areaId: Int)

    fun resetImageState()
}
