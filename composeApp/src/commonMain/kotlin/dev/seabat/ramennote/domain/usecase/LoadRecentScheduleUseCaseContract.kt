package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.model.Schedule

interface LoadRecentScheduleUseCaseContract {
    suspend operator fun invoke(): RunStatus<Schedule?>
}
