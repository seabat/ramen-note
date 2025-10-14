package dev.seabat.ramennote.domain.usecase

import kotlinx.datetime.LocalDate

interface UpdateScheduleInShopUseCaseContract {
    suspend operator fun invoke(shopId: Int, date: LocalDate)
}


