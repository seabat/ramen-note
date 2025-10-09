package dev.seabat.ramennote.domain.usecase

import kotlinx.datetime.LocalDate

interface AddScheduleUseCaseContract {
    suspend operator fun invoke(shopId: Int, date: LocalDate)
}


