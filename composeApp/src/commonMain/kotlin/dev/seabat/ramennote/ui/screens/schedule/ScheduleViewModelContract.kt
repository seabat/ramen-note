package dev.seabat.ramennote.ui.screens.schedule

import dev.seabat.ramennote.domain.model.Schedule
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.LocalDate

interface ScheduleViewModelContract {
    val schedules: StateFlow<List<Schedule>>

    fun loadSchedule()

    fun editSchedule(shopId: Int, date: LocalDate)

    fun deleteSchedule(shopId: Int)
}


