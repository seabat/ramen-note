package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.domain.model.FullReport

interface LoadReportsUseCaseContract {
    suspend operator fun invoke(): List<FullReport>
}