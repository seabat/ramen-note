package dev.seabat.ramennote.ui.screens.history

import dev.seabat.ramennote.domain.model.Area
import dev.seabat.ramennote.domain.model.Shop
import kotlinx.coroutines.flow.StateFlow

interface SelectShopViewModelContract {
    val areas: StateFlow<List<Area>>
    val shopsInArea: StateFlow<List<Shop>>

    fun loadAreas()

    fun loadShopsByArea(areaName: String)
}
