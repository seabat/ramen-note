package dev.seabat.ramennote.ui.screens.history

import dev.seabat.ramennote.domain.model.Report
import kotlinx.datetime.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MockHistoryViewModel : HistoryViewModelContract {

    private val _reports = MutableStateFlow<List<Report>>(emptyList())
    override val reports: StateFlow<List<Report>> = _reports.asStateFlow()

    init {
        // プレビューで LaunchedEffect が動かない場合でも表示されるよう初期化
        loadReports()
    }

    override fun loadReports() {
        _reports.value = listOf(
            Report(
                id = 1,
                menuName = "醤油ラーメン",
                photoName = "report_001.jpg",
                impression = "あっさりしていて飲みやすいスープ。麺との相性も良い。",
                date = LocalDate.parse("2025-08-05")
            ),
            Report(
                id = 2,
                menuName = "味噌ラーメン",
                photoName = "report_002.jpg",
                impression = "濃厚な味噌にバターが効いててコク深い。",
                date = LocalDate.parse("2025-08-12")
            ),
            Report(
                id = 3,
                menuName = "塩ラーメン",
                photoName = "report_003.jpg",
                impression = "透き通ったスープで後味がすっきり。",
                date = LocalDate.parse("2025-07-22")
            ),
            Report(
                id = 4,
                menuName = "つけ麺",
                photoName = "report_004.jpg",
                impression = "魚介豚骨の濃厚つけ汁。麺がモチモチで満足度高い。",
                date = LocalDate.parse("2025-07-03")
            ),
            Report(
                id = 5,
                menuName = "家系ラーメン",
                photoName = "report_005.jpg",
                impression = "ライスが止まらない濃厚スープ。ほうれん草が良いアクセント。",
                date = LocalDate.parse("2025-06-18")
            ),
            Report(
                id = 6,
                menuName = "担々麺",
                photoName = "report_006.jpg",
                impression = "痺れと辛さのバランスが絶妙。胡麻の香りが豊か。",
                date = LocalDate.parse("2025-06-02")
            )
        )
    }
}