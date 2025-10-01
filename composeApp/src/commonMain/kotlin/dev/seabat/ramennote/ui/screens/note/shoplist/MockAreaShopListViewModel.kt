package dev.seabat.ramennote.ui.screens.note.shoplist

import dev.seabat.ramennote.domain.model.Shop
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MockAreaShopListViewModel() : AreaShopListViewModelContract {
    private val _shops: MutableStateFlow<List<Shop>> = MutableStateFlow(listOf())
    override val shops: StateFlow<List<Shop>> = _shops.asStateFlow()
    override fun loadShops(area: String) {
        // Preview用なので何もしない
    }
}