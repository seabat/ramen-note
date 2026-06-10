package dev.seabat.ramennote.ui.screens.note.editarea

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.usecase.DeleteAreaUseCaseContract
import dev.seabat.ramennote.domain.usecase.FetchUnsplashImageUseCaseContract
import dev.seabat.ramennote.domain.usecase.LoadAreaImageUseCaseContract
import dev.seabat.ramennote.domain.usecase.LoadAreasUseCaseContract
import dev.seabat.ramennote.domain.usecase.UpdateAreaUseCaseContract
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditAreaViewModel(
    private val deleteAreaUseCase: DeleteAreaUseCaseContract,
    private val updateAreaUseCase: UpdateAreaUseCaseContract,
    private val fetchUnsplashImageUseCase: FetchUnsplashImageUseCaseContract,
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

    // 新たに取得した画像バイト（null = 画像変更なし）
    private var newImageBytes: ByteArray? = null

    // 現在表示中の画像バイト（エラー後に復元するため保持）
    private var displayedImageBytes: ByteArray? = null

    override fun editArea(areaId: Int, newAreaName: String) {
        viewModelScope.launch {
            _editState.value = RunStatus.Loading()
            _editState.value = updateAreaUseCase(areaId, newAreaName, newImageBytes)
            if (_editState.value is RunStatus.Success) {
                _areaName.value = newAreaName
                newImageBytes = null
            }
        }
    }

    override fun deleteArea(areaId: Int) {
        viewModelScope.launch {
            _deleteState.value = RunStatus.Loading()
            _deleteState.value = deleteAreaUseCase(areaId)
        }
    }

    override fun fetchNewImage(areaName: String) {
        viewModelScope.launch {
            _imageState.value = RunStatus.Loading()
            val result = fetchUnsplashImageUseCase(areaName)
            _imageState.value = result
            if (result is RunStatus.Success) {
                newImageBytes = result.data
                displayedImageBytes = result.data
            }
        }
    }

    override fun loadAreaName(areaId: Int) {
        viewModelScope.launch {
            // areaId からエリア名を取得して _areaName を設定
            val areas = loadAreasUseCase()
            _areaName.value = areas.find { it.areaId == areaId }?.name ?: ""
        }
    }

    override fun loadImage(areaId: Int) {
        viewModelScope.launch {
            _imageState.value = RunStatus.Loading()
            val result = loadAreaImageUseCase(areaId)
            _imageState.value = result
            if (result is RunStatus.Success) {
                displayedImageBytes = result.data
            }
        }
    }

    override fun resetImageState() {
        _imageState.value = displayedImageBytes?.let { RunStatus.Success(it) } ?: RunStatus.Idle()
    }

    override fun resetEditState() {
        _editState.value = RunStatus.Idle()
    }
}
