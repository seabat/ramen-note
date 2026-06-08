package dev.seabat.ramennote.ui.screens.note.shopslocation

import dev.seabat.ramennote.domain.model.Shop
import dev.seabat.ramennote.domain.model.ShopLocation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MockShopsLocationViewModel : ShopsLocationViewModelContract {
    override val locations: StateFlow<List<ShopLocation>> =
        MutableStateFlow(
            listOf(
                ShopLocation(
                    shop = Shop(id = 1, name = "一風堂", areaId = 1, shopUrl = "", mapUrl = "", star = 4, stationName = "渋谷", category = "豚骨"),
                    latitude = 35.6595,
                    longitude = 139.7004
                ),
                ShopLocation(
                    shop =
                        Shop(
                            id = 2,
                            name = "博多風龍",
                            areaId = 1,
                            shopUrl = "",
                            mapUrl = "",
                            star = 3,
                            stationName = "新宿",
                            category = "豚骨"
                        ),
                    latitude = 35.6917,
                    longitude = 139.7006
                )
            )
        )

    override val isLoading: StateFlow<Boolean> = MutableStateFlow(false)

    override fun loadShopsAndResolveLocations(areaId: Int) {}
}
