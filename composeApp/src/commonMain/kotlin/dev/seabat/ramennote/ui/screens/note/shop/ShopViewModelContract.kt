package dev.seabat.ramennote.ui.screens.note.shop

import dev.seabat.ramennote.domain.model.Shop
import kotlinx.coroutines.flow.StateFlow

interface ShopViewModelContract {
    val shopImage: StateFlow<ByteArray?>
    fun loadImage(shop: Shop)
}


