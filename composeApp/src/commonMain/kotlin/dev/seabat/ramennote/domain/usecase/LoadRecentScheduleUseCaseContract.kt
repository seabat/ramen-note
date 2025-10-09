package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.model.Shop

interface LoadRecentScheduleUseCaseContract {
    suspend operator fun invoke(): RunStatus<Shop?>
}
