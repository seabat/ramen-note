package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.domain.model.FullReport
import kotlinx.coroutines.flow.Flow

interface LoadReportsByYearUseCaseContract {
    operator fun invoke(year: Int): Flow<FullReport>
}
