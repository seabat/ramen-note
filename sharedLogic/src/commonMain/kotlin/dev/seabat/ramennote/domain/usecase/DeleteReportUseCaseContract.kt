package dev.seabat.ramennote.domain.usecase

interface DeleteReportUseCaseContract {
    suspend operator fun invoke(reportId: Int)
}
