package dev.seabat.ramennote.ui.screens.schedule

import dev.seabat.ramennote.domain.model.Shop
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.LocalDate

interface ScheduleViewModelContract {
    val scheduledShops: StateFlow<List<Shop>>

    fun loadSchedule()

    fun editSchedule(shopId: Int, date: LocalDate)

    fun deleteSchedule(shopId: Int)
}


