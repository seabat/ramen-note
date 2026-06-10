package dev.seabat.ramennote.ui.screens.note.shop

import dev.seabat.ramennote.domain.model.Shop
import dev.seabat.ramennote.domain.util.createTodayLocalDate
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
                areaId = 1,
                shopUrl = "https://example.com",
                mapUrl = "https://maps.google.com",
                star = 2,
                stationName = "JR渋谷駅",
                category = "家系",
                scheduledDate = createTodayLocalDate(),
                note = "徳島ラーメンの老舗で、濃厚な豚骨醤油スープが特徴です。甘辛く煮込まれた豚バラ肉と生卵をトッピングしていただくのが定番のスタイル。地元の人々はもちろん、観光客にも人気の高いお店です。"
            )
        )
    override val shop: StateFlow<Shop?> = _shop.asStateFlow()

    private val _shopImage = MutableStateFlow<ByteArray?>(null)
    override val shopImage: StateFlow<ByteArray?> = _shopImage

    private val _areaName = MutableStateFlow<String>("東京")
    override val areaName: StateFlow<String> = _areaName.asStateFlow()

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
