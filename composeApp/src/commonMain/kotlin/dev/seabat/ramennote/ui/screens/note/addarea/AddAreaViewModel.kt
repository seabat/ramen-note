package dev.seabat.ramennote.ui.screens.note.addarea

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.seabat.ramennote.data.repository.AreasRepositoryContract
import dev.seabat.ramennote.domain.model.Area
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.usecase.FetchUnsplashImageUseCaseContract
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class AddAreaViewModel(
    private val areasRepository: AreasRepositoryContract,
    private val fetchUnsplashImageUseCase: FetchUnsplashImageUseCaseContract
): ViewModel(), AddAreaViewModelContract {

    private val _imageState: MutableStateFlow<RunStatus<ByteArray?>> =
        MutableStateFlow(RunStatus.Idle())
    override val imageState: StateFlow<RunStatus<ByteArray?>> = _imageState.asStateFlow()

    override fun addArea(area: String) {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        viewModelScope.launch {
            areasRepository.add(
                Area(
                    name = area.trim(),
                    updatedDate = today,
                    count = 0
                )
            )
        }
    }

    override fun fetchImage(area: String) {
        viewModelScope.launch {
            _imageState.value = RunStatus.Loading()
            _imageState.value = fetchUnsplashImageUseCase(area)
        }
    }
}


