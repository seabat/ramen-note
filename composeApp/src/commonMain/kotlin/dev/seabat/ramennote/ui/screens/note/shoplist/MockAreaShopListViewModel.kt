package dev.seabat.ramennote.ui.screens.note.shoplist

import dev.seabat.ramennote.domain.model.Shop
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.LocalDate

class MockAreaShopListViewModel() : AreaShopListViewModelContract {
    private val _shops: MutableStateFlow<List<Shop>> = MutableStateFlow(
        listOf(
            Shop(
                id = 1,
                name = "一風堂 博多本店",
                area = "福岡",
                shopUrl = "https://www.ippudo.com/",
                mapUrl = "https://maps.google.com/",
                star = 5,
                stationName = "博多駅",
                category = "ラーメン",
                scheduledDate = LocalDate.parse("2024-12-25"),
                menuName1 = "白丸元味",
                menuName2 = "赤丸新味",
                menuName3 = "一風堂特製ラーメン",
                photoName1 = "hakata_ramen_1.jpg",
                photoName2 = "hakata_ramen_2.jpg",
                photoName3 = "hakata_ramen_3.jpg",
                description1 = "博多ラーメンの代表格。とんこつスープが絶品",
                description2 = "麺の硬さを選べる本格博多ラーメン",
                description3 = "一風堂オリジナルの特製ラーメン",
                favorite = true
            )
        )
    )
    override val shops: StateFlow<List<Shop>> = _shops.asStateFlow()
    override fun loadShops(area: String) {
        // Preview用なので何もしない
    }
}