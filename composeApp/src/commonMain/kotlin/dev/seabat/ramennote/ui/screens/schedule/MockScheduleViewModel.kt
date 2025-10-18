package dev.seabat.ramennote.ui.screens.schedule

import dev.seabat.ramennote.domain.model.Schedule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.LocalDate

class MockScheduleViewModel : ScheduleViewModelContract {
    private val _schedules: MutableStateFlow<List<Schedule>> = MutableStateFlow(
        listOf(
            Schedule(
                shopId = 101,
                shopName = "中華そば 青葉",
                scheduledDate = LocalDate.parse("2025-11-03"),
                star = 3,
                isReported = false
            ),
            Schedule(
                shopId = 102,
                shopName = "ラーメン 花月",
                scheduledDate = LocalDate.parse("2025-11-20"),
                star = 2,
                isReported = true
            ),
            Schedule(
                shopId = 103,
                shopName = "麺屋 一例",
                scheduledDate = LocalDate.parse("2025-12-05"),
                star = 1,
                isReported = false
            )
        )
    )

    override val schedules: StateFlow<List<Schedule>> = _schedules.asStateFlow()

    override fun loadSchedule() {
        // Preview / Mock 用: すでに初期値を流しているため何もしない
    }

    override fun editSchedule(shopId: Int, date: LocalDate) {
        // Preview / Mock 用
    }

    override fun deleteSchedule(shopId: Int) {
        // Preview / Mock 用
    }
}


