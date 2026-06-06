package dev.seabat.ramennote.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.seabat.ramennote.domain.model.Area
import dev.seabat.ramennote.domain.model.Shop
import dev.seabat.ramennote.domain.usecase.LoadAreasUseCaseContract
import dev.seabat.ramennote.domain.usecase.LoadShopListByAreaUseCaseContract
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SelectShopViewModel(
    private val loadAreasUseCase: LoadAreasUseCaseContract,
    private val loadShopListByAreaUseCase: LoadShopListByAreaUseCaseContract
) : ViewModel(),
    SelectShopViewModelContract {
    private val _areas = MutableStateFlow<List<Area>>(emptyList())
    override val areas: StateFlow<List<Area>> = _areas.asStateFlow()

    private val _shopsInArea = MutableStateFlow<List<Shop>>(emptyList())
    override val shopsInArea: StateFlow<List<Shop>> = _shopsInArea.asStateFlow()

    override fun loadAreas() {
        viewModelScope.launch {
            _areas.value = loadAreasUseCase()
        }
    }

    override fun loadShopsByArea(areaName: String) {
        viewModelScope.launch {
            _shopsInArea.value = loadShopListByAreaUseCase(areaName)
        }
    }
}
