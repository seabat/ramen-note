package dev.seabat.ramennote.ui.screens.history

import dev.seabat.ramennote.domain.model.Area
import dev.seabat.ramennote.domain.model.Shop
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.LocalDate

class MockSelectShopViewModel : SelectShopViewModelContract {
    private val _areas = MutableStateFlow<List<Area>>(emptyList())
    override val areas: StateFlow<List<Area>> = _areas.asStateFlow()

    private val _shopsInArea = MutableStateFlow<List<Shop>>(emptyList())
    override val shopsInArea: StateFlow<List<Shop>> = _shopsInArea.asStateFlow()

    override fun loadAreas() {
        // Preview用なので何もしない
    }

    override fun loadShopsByArea(areaName: String) {
        // Preview用なので何もしない
    }
}

/** エリアと店舗が選択できる状態のプレビュー用モック */
class MockSelectShopViewModelWithData : SelectShopViewModelContract {
    private val _areas =
        MutableStateFlow(
            listOf(
                Area(areaId = 1, name = "渋谷", updatedDate = LocalDate.parse("2025-01-01"), count = 3, sort = 1),
                Area(areaId = 2, name = "新宿", updatedDate = LocalDate.parse("2025-01-01"), count = 2, sort = 2)
            )
        )
    override val areas: StateFlow<List<Area>> = _areas.asStateFlow()

    private val _shopsInArea =
        MutableStateFlow(
            listOf(
                Shop(id = 1, name = "一風堂 渋谷店", areaId = 1),
                Shop(id = 2, name = "麺屋武蔵", areaId = 1)
            )
        )
    override val shopsInArea: StateFlow<List<Shop>> = _shopsInArea.asStateFlow()

    override fun loadAreas() {
        // Preview用なので何もしない
    }

    override fun loadShopsByArea(areaName: String) {
        // Preview用なので何もしない
    }
}
