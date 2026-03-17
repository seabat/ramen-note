package dev.seabat.ramennote.ui.screens.note.editarea

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.usecase.DeleteAreaUseCaseContract
import dev.seabat.ramennote.domain.usecase.LoadAreaImageUseCaseContract
import dev.seabat.ramennote.domain.usecase.LoadAreasUseCaseContract
import dev.seabat.ramennote.domain.usecase.UpdateAreaImageUseCaseContract
import dev.seabat.ramennote.domain.usecase.UpdateAreaUseCaseContract
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditAreaViewModel(
    private val deleteAreaUseCase: DeleteAreaUseCaseContract,
    private val updateAreaUseCase: UpdateAreaUseCaseContract,
    private val updateAreaImageUseCase: UpdateAreaImageUseCaseContract,
    private val loadAreaImageUseCase: LoadAreaImageUseCaseContract,
    private val loadAreasUseCase: LoadAreasUseCaseContract
) : ViewModel(),
    EditAreaViewModelContract {
    private val _deleteState: MutableStateFlow<RunStatus<String>> =
        MutableStateFlow(RunStatus.Idle())
    override val deleteState: StateFlow<RunStatus<String>> = _deleteState.asStateFlow()

    private val _editState: MutableStateFlow<RunStatus<String>> =
        MutableStateFlow(RunStatus.Idle())
    override val editState: StateFlow<RunStatus<String>> = _editState.asStateFlow()

    private val _imageState: MutableStateFlow<RunStatus<ByteArray>> =
        MutableStateFlow(RunStatus.Idle())
    override val imageState: StateFlow<RunStatus<ByteArray>> = _imageState.asStateFlow()

    private val _areaName: MutableStateFlow<String> = MutableStateFlow("")
    override val areaName: StateFlow<String> = _areaName.asStateFlow()

    override fun editArea(areaId: Int, newAreaName: String) {
        viewModelScope.launch {
            _editState.value = RunStatus.Loading()
            _editState.value = updateAreaUseCase(areaId, newAreaName)
            if (_editState.value is RunStatus.Success) {
                _areaName.value = newAreaName
            }
        }
    }

    override fun deleteArea(areaId: Int) {
        viewModelScope.launch {
            _deleteState.value = RunStatus.Loading()
            _editState.value = deleteAreaUseCase(areaId)
        }
    }

    override fun fetchNewImage(areaId: Int) {
        viewModelScope.launch {
            _imageState.value = RunStatus.Loading()
            _imageState.value = updateAreaImageUseCase(areaId)
        }
    }

    override fun loadImage(areaId: Int) {
        viewModelScope.launch {
            // areaId からエリア名を取得して _areaName を設定
            val areas = loadAreasUseCase()
            _areaName.value = areas.find { it.areaId == areaId }?.name ?: ""

            _imageState.value = RunStatus.Loading()
            _imageState.value = loadAreaImageUseCase(areaId)
        }
    }

    override fun resetImageState() {
        _imageState.value = RunStatus.Idle()
    }
}
