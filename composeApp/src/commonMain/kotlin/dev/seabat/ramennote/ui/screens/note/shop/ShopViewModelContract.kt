package dev.seabat.ramennote.ui.screens.note.shop

import dev.seabat.ramennote.domain.model.Shop
import kotlinx.coroutines.flow.StateFlow

interface ShopViewModelContract {
    val shop: StateFlow<Shop?>
    val shopImage: StateFlow<ByteArray?>
    fun loadShopAndImage(id: Int)
}


