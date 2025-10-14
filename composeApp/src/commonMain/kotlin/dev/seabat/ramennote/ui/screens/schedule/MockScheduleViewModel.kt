package dev.seabat.ramennote.ui.screens.schedule

import dev.seabat.ramennote.domain.model.Shop
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.LocalDate

class MockScheduleViewModel : ScheduleViewModelContract {
    private val _scheduledShops: MutableStateFlow<List<Shop>> = MutableStateFlow(
        listOf(
            Shop(
                id = 101,
                name = "中華そば 青葉",
                area = "東京",
                scheduledDate = LocalDate.parse("2025-11-03"),
                star = 3
            ),
            Shop(
                id = 102,
                name = "ラーメン 花月",
                area = "神奈川",
                scheduledDate = LocalDate.parse("2025-11-20"),
                star = 2
            ),
            Shop(
                id = 103,
                name = "麺屋 一例",
                area = "東京",
                scheduledDate = LocalDate.parse("2025-12-05"),
                star = 1
            )
        )
    )

    override val scheduledShops: StateFlow<List<Shop>> = _scheduledShops.asStateFlow()

    private val _reported: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val reported: StateFlow<Boolean> = _reported.asStateFlow()

    override fun loadSchedule() {
        // Preview / Mock 用: すでに初期値を流しているため何もしない
    }

    override fun editSchedule(shopId: Int, date: LocalDate) {
        // Preview / Mock 用
    }

    override fun deleteSchedule(shopId: Int) {
        // Preview / Mock 用
    }

    override fun report(shopId: Int) {
        // Preview / Mock 用
    }
}


