package dev.seabat.ramennote.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.seabat.ramennote.data.repository.AreasRepository
import dev.seabat.ramennote.domain.model.RunStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditAreaViewModel: ViewModel() {

    // TODO: DI 導入時にプライマリコンストラクタに移動
    private val areasRepository: AreasRepository = AreasRepository()

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
