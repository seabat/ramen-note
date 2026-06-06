package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.domain.model.Report

interface UpdateReportUseCaseContract {
    suspend operator fun invoke(report: Report, imageBytes: ByteArray?)
}
