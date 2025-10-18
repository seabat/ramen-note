package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.ReportsRepositoryContract

class DeleteReportUseCase(
    private val reportsRepository: ReportsRepositoryContract
) : DeleteReportUseCaseContract {
    override suspend operator fun invoke(reportId: Int) {
        reportsRepository.delete(reportId)
    }
}
