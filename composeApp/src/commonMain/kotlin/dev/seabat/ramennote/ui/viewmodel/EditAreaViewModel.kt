package dev.seabat.ramennote.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.seabat.ramennote.data.repository.AreasRepository
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

    fun editArea(area: String) {
        viewModelScope.launch {
            _editStatus.value = RunStatus.Loading()
            delay(2000)
            _editStatus.value = RunStatus.Success(data = "")
        }
    }

    fun deleteArea(area: String) {
        viewModelScope.launch {
            _deleteStatus.value = RunStatus.Loading()
            delay(2000)
            _deleteStatus.value = RunStatus.Success(data = "")
        }
    }
}

sealed class RunStatus<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Idle<T> : RunStatus<T>()
    class Success<T>(data: T) : RunStatus<T>(data = data)
    class Error<T>(errorMessage: String) : RunStatus<T>(message = errorMessage)
    class Loading<T> : RunStatus<T>()
}