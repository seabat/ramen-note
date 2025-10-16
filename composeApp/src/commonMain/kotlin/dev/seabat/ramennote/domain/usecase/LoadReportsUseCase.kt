package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.ReportsRepositoryContract
import dev.seabat.ramennote.domain.model.Report

interface LoadReportsUseCaseContract {
    suspend fun invoke(): List<Report>
}

class LoadReportsUseCase(
    private val reportsRepository: ReportsRepositoryContract
): LoadReportsUseCaseContract {
    override suspend fun invoke(): List<Report> = reportsRepository.load()
}


