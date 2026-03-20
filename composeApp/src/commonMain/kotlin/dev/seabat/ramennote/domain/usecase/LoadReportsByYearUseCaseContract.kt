package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.domain.model.FullReport

interface LoadReportsByYearUseCaseContract {
    suspend operator fun invoke(year: Int): List<FullReport>
}
