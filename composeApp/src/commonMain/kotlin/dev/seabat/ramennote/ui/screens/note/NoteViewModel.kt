package dev.seabat.ramennote.ui.screens.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.seabat.ramennote.data.repository.AreasRepositoryContract
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.usecase.LoadImageUseCaseContract
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NoteViewModel(
    private val areasRepository: AreasRepositoryContract,
    private val loadImageListUseCase: LoadImageUseCaseContract
) : ViewModel(),
    NoteViewModelContract {
    private val _areas: MutableStateFlow<List<AreaWithImage>> = MutableStateFlow(emptyList())
    override val areas: StateFlow<List<AreaWithImage>> = _areas.asStateFlow()

    private val _imagesState: MutableStateFlow<RunStatus<List<ByteArray>>> =
        MutableStateFlow(RunStatus.Idle())
    override val imagesState: StateFlow<RunStatus<List<ByteArray>>> = _imagesState.asStateFlow()

    override fun fetchAreas() {
        viewModelScope.launch {
            val area = areasRepository.fetch()
            area
                .map { area ->
                    val image = loadImageListUseCase(area.name)
                    AreaWithImage(
                        name = area.name,
                        count = area.count,
                        updatedDate = area.updatedDate,
                        imageBytes =
                            when (image) {
                                is RunStatus.Success -> image.data
                                else -> null
                            }
                    )
                }.also {
                    _areas.value = it
                }
        }
    }
}
