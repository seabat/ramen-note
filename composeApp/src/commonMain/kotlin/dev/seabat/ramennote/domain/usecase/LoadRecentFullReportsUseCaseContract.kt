package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.domain.model.FullReport

interface LoadRecentFullReportsUseCaseContract {
    suspend operator fun invoke(): List<FullReport>
}
