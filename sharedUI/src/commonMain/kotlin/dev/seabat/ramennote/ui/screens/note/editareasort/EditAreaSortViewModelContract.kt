package dev.seabat.ramennote.ui.screens.note.editareasort

import dev.seabat.ramennote.domain.model.Area
import dev.seabat.ramennote.domain.model.RunStatus
import kotlinx.coroutines.flow.StateFlow

interface EditAreaSortViewModelContract {
    val areas: StateFlow<List<Area>>
    val editAreasState: StateFlow<RunStatus<String>>

    fun loadAreas()

    fun setSort(areaName: String, sort: Int)

    fun editAreaSort()
}
