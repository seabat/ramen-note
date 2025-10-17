package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.ReportsRepositoryContract
import dev.seabat.ramennote.data.repository.LocalImageRepositoryContract
import dev.seabat.ramennote.domain.model.Report
import dev.seabat.ramennote.domain.model.RunStatus

class AddReportUseCase(
    private val createNoImageIfNeededUseCase: CreateNoImageIfNeededUseCaseContract,
    private val reportsRepository: ReportsRepositoryContract,
    private val localAreaImageRepository: LocalImageRepositoryContract
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
            val updatedReport = report.copy(id = newId).let {
                if (report.photoName.isEmpty() || byteArray == null) {
                    it.copy(photoName = reportNoImageFileName)
                } else {
                    it
                }
            }
            
            // 画像を保存
            saveImage(byteArray, updatedReport.photoName)

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

    private suspend fun saveImage(byteArray: ByteArray?, photoName: String) {
        if (byteArray == null) {
            createNoImageIfNeededUseCase(reportNoImageFileName)
        } else {
            localAreaImageRepository.save(byteArray, photoName)
        }
    }
}
