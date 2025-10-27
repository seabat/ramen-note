package dev.seabat.ramennote.ui.screens.home

import dev.seabat.ramennote.domain.model.FullReport
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.model.Schedule
import kotlinx.coroutines.flow.StateFlow

interface HomeViewModelContract {
    val schedule: StateFlow<Schedule?>
    val scheduleState: StateFlow<RunStatus<Schedule?>>
    val favoriteShops: StateFlow<List<ShopWithImage>>
    val threeMonthsReports: StateFlow<List<FullReport>>

    fun loadRecentSchedule()

    fun setScheduleStateToIdle()

    fun loadFavoriteShops()

    fun loadThreeMonthsReports()
}
