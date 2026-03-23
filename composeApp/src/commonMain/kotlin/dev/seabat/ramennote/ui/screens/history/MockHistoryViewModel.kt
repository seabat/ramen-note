package dev.seabat.ramennote.ui.screens.history

import dev.seabat.ramennote.domain.model.FullReport
import dev.seabat.ramennote.domain.model.Shop
import dev.seabat.ramennote.ui.share.XShareLauncher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.LocalDate

class MockHistoryViewModel : HistoryViewModelContract {
    private val _reports = MutableStateFlow<List<FullReport>>(emptyList())
    override val reports: StateFlow<List<FullReport>> = _reports.asStateFlow()

    private val _shopName = MutableStateFlow<String>("")
    override val shopName: StateFlow<String> = _shopName.asStateFlow()

    private val _areaName = MutableStateFlow<String>("")
    override val areaName: StateFlow<String> = _areaName.asStateFlow()

    private val _shops = MutableStateFlow<List<Shop>>(emptyList())
    override val shops: StateFlow<List<Shop>> = _shops.asStateFlow()

    private val _year = MutableStateFlow<String>("")
    override val year: StateFlow<String> = _year.asStateFlow()

    private val _selectableYears = MutableStateFlow<List<Int>>(emptyList())
    override val selectableYears: StateFlow<List<Int>> = _selectableYears.asStateFlow()

    init {
        // プレビューで LaunchedEffect が動かない場合でも表示されるよう初期化
        loadReports()
    }

    override fun loadReports() {
        _reports.value =
            listOf(
                FullReport(
                    id = 1,
                    shopId = 1,
                    shopName = "○○ラーメン",
                    menuName = "醤油ラーメン",
                    photoName = "report_001.jpg",
                    imageBytes = null,
                    impression = "あっさりしていて飲みやすいスープ。麺との相性も良い。",
                    date = LocalDate.parse("2025-08-05"),
                    star = 1
                ),
                FullReport(
                    id = 2,
                    shopId = 2,
                    shopName = "△△家",
                    menuName = "味噌ラーメン",
                    photoName = "report_002.jpg",
                    imageBytes = null,
                    impression = "濃厚な味噌にバターが効いててコク深い。",
                    date = LocalDate.parse("2025-08-12"),
                    star = 1
                ),
                FullReport(
                    id = 3,
                    shopId = 3,
                    shopName = "□□軒",
                    menuName = "塩ラーメン",
                    photoName = "report_003.jpg",
                    imageBytes = null,
                    impression = "透き通ったスープで後味がすっきり。",
                    date = LocalDate.parse("2025-07-22"),
                    star = 2
                ),
                FullReport(
                    id = 4,
                    shopId = 4,
                    shopName = "◇◇食堂",
                    menuName = "つけ麺",
                    photoName = "report_004.jpg",
                    imageBytes = null,
                    impression = "魚介豚骨の濃厚つけ汁。麺がモチモチで満足度高い。",
                    date = LocalDate.parse("2025-07-03"),
                    star = 2
                ),
                FullReport(
                    id = 5,
                    shopId = 5,
                    shopName = "☆☆家",
                    menuName = "家系ラーメン",
                    photoName = "report_005.jpg",
                    imageBytes = null,
                    impression = "ライスが止まらない濃厚スープ。ほうれん草が良いアクセント。",
                    date = LocalDate.parse("2025-06-18"),
                    star = 3
                ),
                FullReport(
                    id = 6,
                    shopId = 6,
                    shopName = "◎◎ラーメン",
                    menuName = "担々麺",
                    photoName = "report_006.jpg",
                    imageBytes = null,
                    impression = "痺れと辛さのバランスが絶妙。胡麻の香りが豊か。",
                    date = LocalDate.parse("2025-06-02"),
                    star = 3
                )
            )
        _selectableYears.value =
            _reports.value
                .map { it.date.year }
                .distinct()
                .sortedDescending()
    }

    override fun loadReportsByShop(shopId: Int) {
        // Preview用なので何もしない
    }

    override fun loadReportsByArea(areaId: Int) {
        // Preview用なので何もしない
    }

    override fun loadReportsByYear(year: Int) {
        // Preview用なので何もしない
    }

    override fun shareToX(
        postText: String,
        photoName: String,
        xShareLauncher: XShareLauncher
    ) {
        // Preview用なので何もしない
    }

    override fun searchShops(query: String) {
        // Preview用なので何もしない
    }
}

/** 店舗検索でヒットがある状態のプレビュー用モック */
class MockHistoryViewModelWithSearchHits : HistoryViewModelContract by MockHistoryViewModel() {
    private val _shops =
        MutableStateFlow(
            listOf(
                Shop(id = 1, name = "一風堂 渋谷店", areaId = 1),
                Shop(id = 2, name = "一風堂 新宿店", areaId = 1),
                Shop(id = 3, name = "一風堂 池袋店", areaId = 2)
            )
        )
    override val shops: StateFlow<List<Shop>> = _shops.asStateFlow()

    override fun searchShops(query: String) {
        // Preview用なので何もしない
    }
}
