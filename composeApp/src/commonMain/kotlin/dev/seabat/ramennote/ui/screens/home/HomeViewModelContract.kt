package dev.seabat.ramennote.ui.screens.home

import dev.seabat.ramennote.domain.model.FullReport
import dev.seabat.ramennote.domain.model.MonthlyReportCount
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.model.Schedule
import dev.seabat.ramennote.ui.share.XShareLauncher
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.LocalDate

interface HomeViewModelContract {
    val schedule: StateFlow<Schedule?>
    val loadedScheduleState: StateFlow<RunStatus<Schedule?>>
    val addedScheduleState: StateFlow<RunStatus<String>>
    val favoriteShops: StateFlow<List<ShopWithImage>>
    val recentReports: StateFlow<List<FullReport>>
    val yearlyReportStats: StateFlow<List<MonthlyReportCount>>

    fun shareToX(postText: String, photoName: String, xShareLauncher: XShareLauncher)

    fun loadRecentSchedule()

    fun setLoadedScheduleStateToIdle()

    fun setAddedScheduleStateToIdle()

    fun loadFavoriteShops()

    fun loadRecentReports()

    fun loadYearlyReportStats()

    fun addSchedule(shopId: Int, date: LocalDate)
}
