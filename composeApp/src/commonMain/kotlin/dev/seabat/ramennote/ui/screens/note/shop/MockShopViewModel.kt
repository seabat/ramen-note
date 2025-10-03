package dev.seabat.ramennote.ui.screens.note.shop

import dev.seabat.ramennote.domain.model.Shop
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MockShopViewModel : ShopViewModelContract {
    private val _shopImage = MutableStateFlow<ByteArray?>(null)
    override val shopImage: StateFlow<ByteArray?> = _shopImage

    override fun loadImage(shop: Shop) {
        _shopImage.value = null
    }
}


