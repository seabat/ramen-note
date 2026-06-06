package dev.seabat.ramennote.ui.screens.note

import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.model.Shop
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.LocalDate

/**
 * Preview用のモックViewModel
 * 実際のデータベースアクセスを行わず、固定のデータを返す
 */
class MockNoteViewModel : NoteViewModelContract {
    private val _areas: MutableStateFlow<List<AreaWithImage>> =
        MutableStateFlow(
            listOf(
                AreaWithImage(areaId = 1, name = "東京", updatedDate = LocalDate(2024, 9, 1), count = 12),
                AreaWithImage(areaId = 2, name = "神奈川", updatedDate = LocalDate(2024, 8, 21), count = 5),
                AreaWithImage(areaId = 3, name = "徳島", updatedDate = LocalDate(2024, 7, 3), count = 2),
                AreaWithImage(areaId = 4, name = "愛媛", updatedDate = LocalDate(2024, 6, 14), count = 7)
            )
        )
    override val areas: StateFlow<List<AreaWithImage>> = _areas.asStateFlow()

    private val _shops =
        MutableStateFlow<List<Shop>>(
            listOf(
                Shop(
                    id = 1,
                    name = "一風堂 博多本店",
                    areaId = 1,
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
            )
        )
    override val shops: StateFlow<List<Shop>> = _shops.asStateFlow()

    private val _imagesState: MutableStateFlow<RunStatus<List<ByteArray>>> = MutableStateFlow(RunStatus.Idle())
    override val imagesState: StateFlow<RunStatus<List<ByteArray>>> = _imagesState.asStateFlow()

    override fun fetchAreas() {
        // Preview用なので何もしない
    }

    override fun searchShops(query: String) {
        // Preview用なので何もしない
    }
}
