package dev.seabat.ramennote.ui.screens.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.model.Shop
import dev.seabat.ramennote.domain.usecase.LoadAreasUseCaseContract
import dev.seabat.ramennote.domain.usecase.LoadImagePathUseCaseContract
import dev.seabat.ramennote.domain.usecase.SearchShopsByNameUseCaseContract
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NoteViewModel(
    private val loadAreasUseCase: LoadAreasUseCaseContract,
    private val loadImagePathUseCase: LoadImagePathUseCaseContract,
    private val searchShopsByNameUseCase: SearchShopsByNameUseCaseContract
) : ViewModel(),
    NoteViewModelContract {
    private val _areas: MutableStateFlow<List<AreaWithImage>> = MutableStateFlow(emptyList())
    override val areas: StateFlow<List<AreaWithImage>> = _areas.asStateFlow()

    private val _shops = MutableStateFlow<List<Shop>>(emptyList())
    override val shops: StateFlow<List<Shop>> = _shops.asStateFlow()

    private val _imagesState: MutableStateFlow<RunStatus<List<ByteArray>>> =
        MutableStateFlow(RunStatus.Idle())
    override val imagesState: StateFlow<RunStatus<List<ByteArray>>> = _imagesState.asStateFlow()

    override fun fetchAreas() {
        viewModelScope.launch {
            val area = loadAreasUseCase()
            area
                .map { area ->
                    AreaWithImage(
                        areaId = area.areaId,
                        name = area.name,
                        count = area.count,
                        updatedDate = area.updatedDate,
                        imagePath = loadImagePathUseCase(area.name)
                    )
                }.also {
                    _areas.value = it
                }
        }
    }

    override fun searchShops(query: String) {
        viewModelScope.launch {
            _shops.value = searchShopsByNameUseCase(query)
        }
    }
}
