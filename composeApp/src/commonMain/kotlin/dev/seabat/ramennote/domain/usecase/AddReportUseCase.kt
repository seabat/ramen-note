package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.ReportsRepositoryContract
import dev.seabat.ramennote.data.repository.LocalAreaImageRepositoryContract
import dev.seabat.ramennote.domain.model.Report
import dev.seabat.ramennote.domain.model.RunStatus

class AddReportUseCase(
    private val reportsRepository: ReportsRepositoryContract,
    private val localAreaImageRepository: LocalAreaImageRepositoryContract
) : AddReportUseCaseContract {
    
    override suspend operator fun invoke(report: Report, byteArray: ByteArray?): RunStatus<Int> {
        return try {
            // 最新のReportのIDを取得して+1
            val newId = reportsRepository.load().let { reports ->
                val maxId = if (reports.isNotEmpty()) {
                    reports.maxOf { it.id }
                } else {
                    0
                }
                maxId + 1
            }

            // ReportのIDを更新
            val updatedReport = report.copy(id = newId)
            
            // 画像を保存
            if (byteArray != null) {
                localAreaImageRepository.save(byteArray, updatedReport.photoName)
            }
            
            // SQLiteに保存
            reportsRepository.insert(updatedReport)
            
            // 保存後の最大IDを取得
            val savedReports = reportsRepository.load()
            val finalMaxId = if (savedReports.isNotEmpty()) {
                savedReports.maxOf { it.id }
            } else {
                newId
            }
            
            RunStatus.Success(finalMaxId)
        } catch (e: Exception) {
            RunStatus.Error(e.message ?: "レポートの保存に失敗しました")
        }
    }
}
