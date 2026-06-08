package dev.seabat.ramennote.ui.screens.note.shopslocation

import dev.seabat.ramennote.domain.model.ShopLocation
import kotlinx.coroutines.flow.StateFlow

interface ShopsLocationViewModelContract {
    val locations: StateFlow<List<ShopLocation>>
    val isLoading: StateFlow<Boolean>

    fun loadShopsAndResolveLocations(areaId: Int)
}
