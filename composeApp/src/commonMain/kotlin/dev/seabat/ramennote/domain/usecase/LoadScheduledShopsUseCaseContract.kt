package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.domain.model.Schedule

interface LoadScheduledShopsUseCaseContract {
    suspend operator fun invoke(): List<Schedule>
}


