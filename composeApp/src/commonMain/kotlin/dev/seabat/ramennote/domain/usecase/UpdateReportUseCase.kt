package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.LocalImageRepositoryContract
import dev.seabat.ramennote.data.repository.ReportsRepositoryContract
import dev.seabat.ramennote.domain.model.Report

class UpdateReportUseCase(
    private val localImageRepository: LocalImageRepositoryContract,
    private val reportsRepository: ReportsRepositoryContract
) : UpdateReportUseCaseContract {
    override suspend operator fun invoke(report: Report, imageBytes: ByteArray?) {
        reportsRepository.update(report)
        if (imageBytes != null) {
            localImageRepository.save(name =report.photoName, imageBytes = imageBytes)
        }
    }
}
