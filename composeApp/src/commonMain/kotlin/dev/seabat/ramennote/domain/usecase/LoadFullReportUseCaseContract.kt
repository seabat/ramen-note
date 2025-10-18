package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.domain.model.FullReport

interface LoadFullReportUseCaseContract {
    suspend operator fun invoke(reportId: Int): FullReport?
}