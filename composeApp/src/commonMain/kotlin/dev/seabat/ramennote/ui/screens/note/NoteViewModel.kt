package dev.seabat.ramennote.ui.screens.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import dev.seabat.ramennote.data.repository.AreasRepositoryContract
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.usecase.LoadImageListUseCaseContract
import kotlinx.coroutines.launch

class NoteViewModel(
    private val areasRepository: AreasRepositoryContract,
    private val loadImageListUseCase: LoadImageListUseCaseContract
): ViewModel(), NoteViewModelContract {

    private val _areas: MutableStateFlow<List<AreaWithImage>> = MutableStateFlow(emptyList())
    override val areas: StateFlow<List<AreaWithImage>> = _areas.asStateFlow()

    private val _imagesState: MutableStateFlow<RunStatus<List<ByteArray>>> =
        MutableStateFlow(RunStatus.Idle())
    override val imagesState: StateFlow<RunStatus<List<ByteArray>>> = _imagesState.asStateFlow()

    override fun fetchAreas() {
        viewModelScope.launch{
            val area = areasRepository.fetch()
            val images = loadImageListUseCase()
            area.map { area ->
                AreaWithImage(
                    name = area.name,
                    count = area.count,
                    updatedDate = area.updatedDate,
                    imageBytes = when(images) {
                        is RunStatus.Success -> images.data?.get(0)
                        else -> null
                    }
                )
            }.also {
                _areas.value = it
            }
        }
    }
}


