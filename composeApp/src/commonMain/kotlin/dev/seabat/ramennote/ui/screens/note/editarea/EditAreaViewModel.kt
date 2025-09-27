package dev.seabat.ramennote.ui.screens.note.editarea

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.usecase.DeleteAreaUseCaseContract
import dev.seabat.ramennote.domain.usecase.FetchImageUseCaseContract
import dev.seabat.ramennote.domain.usecase.LoadImageUseCaseContract
import dev.seabat.ramennote.domain.usecase.UpdateAreaUseCaseContract
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditAreaViewModel(
    private val deleteAreaUseCase: DeleteAreaUseCaseContract,
    private val updateAreaUseCase: UpdateAreaUseCaseContract,
    private val fetchImageUseCase: FetchImageUseCaseContract,
    private val loadImageUseCase: LoadImageUseCaseContract
): ViewModel(), EditAreaViewModelContract {

    private val _deleteState: MutableStateFlow<RunStatus<String>> =
        MutableStateFlow(RunStatus.Idle())
    override val deleteState: StateFlow<RunStatus<String>> = _deleteState.asStateFlow()

    private val _editState: MutableStateFlow<RunStatus<String>> =
        MutableStateFlow(RunStatus.Idle())
    override val editState: StateFlow<RunStatus<String>> = _editState.asStateFlow()

    private val _imageState: MutableStateFlow<RunStatus<ByteArray?>> =
        MutableStateFlow(RunStatus.Idle())
    override val imageState: StateFlow<RunStatus<ByteArray?>> = _imageState.asStateFlow()

    override var currentAreaName = ""

    override fun editArea(newAreaName: String) {
        viewModelScope.launch {
            _editState.value = RunStatus.Loading()
            _editState.value = updateAreaUseCase(currentAreaName, newAreaName)
            currentAreaName = newAreaName
        }
    }

    override fun deleteArea(areaName: String) {
        viewModelScope.launch {
            _deleteState.value = RunStatus.Loading()
            _editState.value = deleteAreaUseCase( areaName)
        }
    }

    override fun fetchImage() {
        viewModelScope.launch {
            _imageState.value = RunStatus.Loading()
            _imageState.value = fetchImageUseCase()
        }
    }

    override fun loadImage(name: String) {
        viewModelScope.launch {
            _imageState.value = RunStatus.Loading()
            _imageState.value = loadImageUseCase(name)
        }
    }
}