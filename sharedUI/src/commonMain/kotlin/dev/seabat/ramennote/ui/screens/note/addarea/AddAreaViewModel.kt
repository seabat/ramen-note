package dev.seabat.ramennote.ui.screens.note.addarea

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.usecase.AddAreaUseCaseContract
import dev.seabat.ramennote.domain.usecase.FetchAndSaveUnsplashImageUseCaseContract
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddAreaViewModel(
    private val addAreaUseCase: AddAreaUseCaseContract,
    private val fetchUnsplashImageUseCase: FetchAndSaveUnsplashImageUseCaseContract
) : ViewModel(),
    AddAreaViewModelContract {
    private val _addState: MutableStateFlow<RunStatus<ByteArray>> =
        MutableStateFlow(RunStatus.Idle())
    override val addState: StateFlow<RunStatus<ByteArray>> = _addState.asStateFlow()

    override fun addArea(area: String) {
        viewModelScope.launch {
            _addState.value = RunStatus.Loading()
            when (val result = addAreaUseCase(area)) {
                is RunStatus.Success -> {
                    // エリア登録成功後に Unsplash から画像を取得する
                    _addState.value = fetchUnsplashImageUseCase(area)
                }
                is RunStatus.Error -> {
                    _addState.value = RunStatus.Error(result.message ?: "エリアの登録に失敗しました")
                }
                else -> Unit
            }
        }
    }
}
