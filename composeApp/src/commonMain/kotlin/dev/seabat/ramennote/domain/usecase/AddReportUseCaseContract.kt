package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.domain.model.Report
import dev.seabat.ramennote.domain.model.RunStatus

interface AddReportUseCaseContract {
    suspend operator fun invoke(report: Report, byteArray: ByteArray?): RunStatus<Int>
}