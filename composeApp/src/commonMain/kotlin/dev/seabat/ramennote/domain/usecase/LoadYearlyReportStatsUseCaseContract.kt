package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.domain.model.MonthlyReportCount

/**
 * 過去1年間の月別訪問回数を集計するUseCase
 */
interface LoadYearlyReportStatsUseCaseContract {
    suspend operator fun invoke(): List<MonthlyReportCount>
}

