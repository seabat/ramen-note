package dev.seabat.ramennote.ui.screens.home

import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.model.Shop
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.LocalDate

class MockHomeViewModel : HomeViewModelContract {
    private val _scheduleShop = MutableStateFlow<Shop>(Shop())
    override val scheduledShop: StateFlow<Shop> = _scheduleShop.asStateFlow()

    private val _scheduleShopState = MutableStateFlow<RunStatus<Shop?>>(RunStatus.Idle())
    override val scheduledShopState: StateFlow<RunStatus<Shop?>> = _scheduleShopState.asStateFlow()

    private val _favoriteShops = MutableStateFlow<List<ShopWithImage>>(
        listOf(
            ShopWithImage(
                shop = Shop(
                    id = 1,
                    name = "一風堂 博多本店",
                    area = "福岡",
                    shopUrl = "https://www.ippudo.com/",
                    mapUrl = "https://maps.google.com/",
                    star = 3,
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
            ),
            ShopWithImage(
                shop = Shop(
                    id = 2,
                    name = "一風堂 山口店",
                    area = "山口",
                    shopUrl = "https://www.ippudo.com/",
                    mapUrl = "https://maps.google.com/",
                    star = 3,
                    stationName = "山口駅",
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
            ),
            ShopWithImage(
                shop = Shop(
                    id = 3,
                    name = "一風堂 広島店",
                    area = "広島",
                    shopUrl = "https://www.ippudo.com/",
                    mapUrl = "https://maps.google.com/",
                    star = 3,
                    stationName = "広島駅",
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
            ),
            ShopWithImage(
                shop = Shop(
                    id = 4,
                    name = "一風堂 倉敷店",
                    area = "岡山",
                    shopUrl = "https://www.ippudo.com/",
                    mapUrl = "https://maps.google.com/",
                    star = 3,
                    stationName = "倉敷駅",
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
    )

    override val favoriteShops: StateFlow<List<ShopWithImage>> = _favoriteShops.asStateFlow()

    override fun loadRecentSchedule() {
        // Preview用なので何もしない
    }

    override fun setScheduledShopStateToIdle() {
        // Preview用なので何もしない
    }

    override fun loadFavoriteShops() {
        // Preview用なので何もしない
    }
}