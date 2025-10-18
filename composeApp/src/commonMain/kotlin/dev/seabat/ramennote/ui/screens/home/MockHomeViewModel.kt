package dev.seabat.ramennote.ui.screens.home

import dev.seabat.ramennote.domain.model.FullReport
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.model.Schedule
import dev.seabat.ramennote.domain.model.Shop
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.LocalDate

class MockHomeViewModel : HomeViewModelContract {

    private val _schedule = MutableStateFlow<Schedule?>(Schedule())
    override val schedule: StateFlow<Schedule?> = _schedule.asStateFlow()

    private val _scheduleState = MutableStateFlow<RunStatus<Schedule?>>(RunStatus.Idle())
    override val scheduleState: StateFlow<RunStatus<Schedule?>> = _scheduleState.asStateFlow()

    private val _threeMonthsReports = MutableStateFlow<List<FullReport>>(
        listOf(
            FullReport(
                id = 1,
                shopId = 1,
                shopName = "一風堂 博多本店",
                menuName = "白丸元味",
                photoName = "hakata_ramen_1.jpg",
                imageBytes = null,
                impression = "とんこつスープが濃厚で美味しかった。麺も硬めで好みの硬さだった。",
                date = LocalDate.parse("2024-12-15"),
                star = 1
            ),
            FullReport(
                id = 2,
                shopId = 2,
                shopName = "一風堂 山口店",
                menuName = "赤丸新味",
                photoName = "hakata_ramen_2.jpg",
                imageBytes = null,
                impression = "赤丸新味の辛さがちょうど良く、スープとのバランスが絶妙だった。",
                date = LocalDate.parse("2024-12-10"),
                star = 2
            ),
            FullReport(
                id = 3,
                shopId = 3,
                shopName = "一風堂 広島店",
                menuName = "一風堂特製ラーメン",
                photoName = "hakata_ramen_3.jpg",
                imageBytes = null,
                impression = "特製ラーメンは具材が豊富で、ボリューム満点だった。",
                date = LocalDate.parse("2024-12-05"),
                star = 3
            ),
            FullReport(
                id = 4,
                shopId = 4,
                shopName = "一風堂 倉敷店",
                menuName = "白丸元味",
                photoName = "hakata_ramen_4.jpg",
                imageBytes = null,
                impression = "倉敷店でも本店と同じ味を楽しめた。また来たい。",
                date = LocalDate.parse("2024-11-28"),
                star = 3
            ),
            FullReport(
                id = 5,
                shopId = 5,
                shopName = "一風堂 博多本店",
                menuName = "赤丸新味",
                photoName = "hakata_ramen_5.jpg",
                imageBytes = null,
                impression = "2回目の訪問。前回より辛さを調整してもらった。",
                date = LocalDate.parse("2024-11-20"),
                star = 3
            )
        )
    )
    override val threeMonthsReports: StateFlow<List<FullReport>> = _threeMonthsReports.asStateFlow()

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

    override fun setScheduleStateToIdle() {
        // Preview用なので何もしない
    }

    override fun loadFavoriteShops() {
        // Preview用なので何もしない
    }

    override fun loadThreeMonthsReports() {
        // Preview用なので何もしない
    }
}