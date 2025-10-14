package dev.seabat.ramennote.ui.screens.schedule

import dev.seabat.ramennote.domain.model.Shop
import kotlinx.coroutines.flow.StateFlow

interface ScheduleViewModelContract {
    val scheduledShops: StateFlow<List<Shop>>
    fun loadSchedule()
}


