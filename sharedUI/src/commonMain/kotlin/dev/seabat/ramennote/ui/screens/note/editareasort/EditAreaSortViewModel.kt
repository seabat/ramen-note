package dev.seabat.ramennote.ui.screens.note.editareasort

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.seabat.ramennote.domain.model.Area
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.usecase.LoadAreasUseCaseContract
import dev.seabat.ramennote.domain.usecase.UpdateAllAreasUseCaseContract
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditAreaSortViewModel(
    private val loadAreasUseCase: LoadAreasUseCaseContract,
    private val updateAllAreasUseCase: UpdateAllAreasUseCaseContract
) : ViewModel(),
    EditAreaSortViewModelContract {
    private val _areas: MutableStateFlow<List<Area>> = MutableStateFlow(emptyList())
    override val areas: StateFlow<List<Area>> = _areas.asStateFlow()

    private val _editAreasState: MutableStateFlow<RunStatus<String>> =
        MutableStateFlow(RunStatus.Idle())
    override val editAreasState: StateFlow<RunStatus<String>> = _editAreasState.asStateFlow()

    override fun loadAreas() {
        viewModelScope.launch {
            _areas.value = loadAreasUseCase()
        }
    }

    override fun setSort(areaName: String, sort: Int) {
        _areas.value =
            _areas.value.map { area ->
                if (area.name == areaName) {
                    area.copy(sort = sort)
                } else {
                    area
                }
            }
    }

    override fun editAreaSort() {
        viewModelScope.launch {
            _editAreasState.value = RunStatus.Loading()
            _editAreasState.value = updateAllAreasUseCase(_areas.value)
        }
    }
}
