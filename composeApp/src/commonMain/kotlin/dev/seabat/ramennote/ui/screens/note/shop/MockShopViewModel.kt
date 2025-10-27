package dev.seabat.ramennote.ui.screens.note.shop

import dev.seabat.ramennote.domain.model.Shop
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.LocalDate

class MockShopViewModel : ShopViewModelContract {
    private val _shop =
        MutableStateFlow<Shop?>(
            Shop(
                id = 1,
                name = "XXXX家",
                area = "東京",
                shopUrl = "https://example.com",
                mapUrl = "https://maps.google.com",
                star = 2,
                stationName = "JR渋谷駅",
                category = "家系"
            )
        )
    override val shop: StateFlow<Shop?> = _shop.asStateFlow()

    private val _shopImage = MutableStateFlow<ByteArray?>(null)
    override val shopImage: StateFlow<ByteArray?> = _shopImage

    override fun loadShopAndImage(id: Int) {
        _shopImage.value = null
    }

    override fun addSchedule(shopId: Int, date: LocalDate) {
        // Preview用なので何もしない
    }

    override fun switchFavorite(onOff: Boolean, shopId: Int) {
        // Preview用なので何もしない
    }

    override fun updateStar(star: Int, shopId: Int) {
        // Preview用なので何もしない
    }
}
