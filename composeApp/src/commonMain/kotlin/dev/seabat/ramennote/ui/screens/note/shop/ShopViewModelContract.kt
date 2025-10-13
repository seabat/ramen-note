package dev.seabat.ramennote.ui.screens.note.shop

import dev.seabat.ramennote.domain.model.Shop
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.LocalDate

interface ShopViewModelContract {
    val shop: StateFlow<Shop?>
    val shopImage: StateFlow<ByteArray?>
    fun loadShopAndImage(id: Int)
    fun addSchedule(shopId: Int, date: LocalDate)
    fun switchFavorite(onOff: Boolean, shopId: Int)
    fun updateStar(star: Int, shopId: Int)
}


