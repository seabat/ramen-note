package dev.seabat.ramennote.ui.screens.home

import dev.seabat.ramennote.domain.model.Schedule
import dev.seabat.ramennote.domain.model.RunStatus
import kotlinx.coroutines.flow.StateFlow

interface HomeViewModelContract {
    val schedule: StateFlow<Schedule?>
    val scheduleState: StateFlow<RunStatus<Schedule?>>
    val favoriteShops: StateFlow<List<ShopWithImage>>
    fun loadRecentSchedule()
    fun setScheduleStateToIdle()
    fun loadFavoriteShops()
}