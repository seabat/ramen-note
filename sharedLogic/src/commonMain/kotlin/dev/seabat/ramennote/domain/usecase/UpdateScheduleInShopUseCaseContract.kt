package dev.seabat.ramennote.domain.usecase

import kotlinx.datetime.LocalDate

/**
 * スケジュールの追加・更新
 *
 */
interface UpdateScheduleInShopUseCaseContract {
    suspend operator fun invoke(shopId: Int, date: LocalDate)
}
