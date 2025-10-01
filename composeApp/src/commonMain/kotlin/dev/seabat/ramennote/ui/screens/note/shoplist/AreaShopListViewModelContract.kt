package dev.seabat.ramennote.ui.screens.note.shoplist

import dev.seabat.ramennote.domain.model.Shop
import kotlinx.coroutines.flow.StateFlow

interface AreaShopListViewModelContract {
    val shops: StateFlow<List<Shop>>
    fun loadShops(area: String)
}