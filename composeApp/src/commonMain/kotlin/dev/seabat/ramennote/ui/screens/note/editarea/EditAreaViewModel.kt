package dev.seabat.ramennote.ui.screens.note.editarea

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.seabat.ramennote.data.repository.AreasRepositoryContract
import dev.seabat.ramennote.data.repository.AreaImageRepositoryContract
import dev.seabat.ramennote.domain.model.RunStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditAreaViewModel(
    private val areasRepository: AreasRepositoryContract,
    private val areaImageRepository: AreaImageRepositoryContract
): ViewModel(),
    EditAreaViewModelContract {

    private val _deleteStatus: MutableStateFlow<RunStatus<String>> =
        MutableStateFlow(RunStatus.Idle())
    override val deleteStatus: StateFlow<RunStatus<String>> = _deleteStatus.asStateFlow()

    private val _editStatus: MutableStateFlow<RunStatus<String>> =
        MutableStateFlow(RunStatus.Idle())
    override val editStatus: StateFlow<RunStatus<String>> = _editStatus.asStateFlow()

    override var currentAreas = ""

    private val _imageBytes: MutableStateFlow<ByteArray?> = MutableStateFlow(null)
    override val imageBytes: StateFlow<ByteArray?> = _imageBytes.asStateFlow()

    override fun editArea(newArea: String) {
        viewModelScope.launch {
            _editStatus.value = RunStatus.Loading()
            _editStatus.value = areasRepository.edit(currentAreas, newArea)
            currentAreas = newArea
        }
    }

    override fun deleteArea(area: String) {
        viewModelScope.launch {
            _deleteStatus.value = RunStatus.Loading()
            _editStatus.value = areasRepository.delete( area)
        }
    }

    override fun fetchImage() {
        viewModelScope.launch {
            _imageBytes.value = areaImageRepository.fetch()
        }
    }
}