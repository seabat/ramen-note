package dev.seabat.ramennote.ui.screens.home

import dev.seabat.ramennote.domain.model.FullReport
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.model.Schedule
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.LocalDate

interface HomeViewModelContract {
    val schedule: StateFlow<Schedule?>
    val loadedScheduleState: StateFlow<RunStatus<Schedule?>>
    val addedScheduleState: StateFlow<RunStatus<String>>
    val favoriteShops: StateFlow<List<ShopWithImage>>
    val threeMonthsReports: StateFlow<List<FullReport>>

    fun loadRecentSchedule()

    fun setLoadedScheduleStateToIdle()

    fun setAddedScheduleStateToIdle()

    fun loadFavoriteShops()

    fun loadThreeMonthsReports()

    fun addSchedule(shopId: Int, date: LocalDate)
}
