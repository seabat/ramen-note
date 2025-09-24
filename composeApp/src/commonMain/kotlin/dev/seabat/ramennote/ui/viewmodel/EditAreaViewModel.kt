package dev.seabat.ramennote.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.seabat.ramennote.data.repository.AreasRepositoryContract
import dev.seabat.ramennote.domain.model.RunStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditAreaViewModel(private val areasRepository: AreasRepositoryContract): ViewModel() {

    private val _deleteStatus: MutableStateFlow<RunStatus<String>> = MutableStateFlow(RunStatus.Idle())
    val deleteStatus: StateFlow<RunStatus<String>> = _deleteStatus.asStateFlow()

    private val _editStatus: MutableStateFlow<RunStatus<String>> = MutableStateFlow(RunStatus.Idle())
    val editStatus: StateFlow<RunStatus<String>> = _editStatus.asStateFlow()

    var currentAreas = ""

    fun editArea(newArea: String) {
        viewModelScope.launch {
            _editStatus.value = RunStatus.Loading()
            _editStatus.value = areasRepository.edit(currentAreas, newArea)
            currentAreas = newArea
        }
    }

    fun deleteArea(area: String) {
        viewModelScope.launch {
            _deleteStatus.value = RunStatus.Loading()
            _editStatus.value = areasRepository.delete( area)
        }
    }
}
