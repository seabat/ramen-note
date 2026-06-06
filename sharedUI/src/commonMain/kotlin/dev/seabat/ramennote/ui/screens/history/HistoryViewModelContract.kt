package dev.seabat.ramennote.ui.screens.history

import dev.seabat.ramennote.domain.model.FullReport
import dev.seabat.ramennote.domain.model.Shop
import dev.seabat.ramennote.ui.share.XShareLauncher
import kotlinx.coroutines.flow.StateFlow

interface HistoryViewModelContract {
    val reports: StateFlow<List<FullReport>>
    val shopName: StateFlow<String>
    val areaName: StateFlow<String>
    val shops: StateFlow<List<Shop>>
    val year: StateFlow<String>
    val selectableYears: StateFlow<List<Int>>

    fun loadReports()

    fun loadReportsByShop(shopId: Int)

    fun loadReportsByArea(areaId: Int)

    fun loadReportsByYear(year: Int)

    fun shareToX(postText: String, photoName: String, xShareLauncher: XShareLauncher)

    fun searchShops(query: String)
}
