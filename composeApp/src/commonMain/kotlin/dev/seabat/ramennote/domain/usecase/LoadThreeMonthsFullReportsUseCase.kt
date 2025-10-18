package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.ReportsRepositoryContract
import dev.seabat.ramennote.data.repository.ShopsRepositoryContract
import dev.seabat.ramennote.data.repository.LocalImageRepositoryContract
import dev.seabat.ramennote.domain.model.FullReport
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.minus

class LoadThreeMonthsFullReportsUseCase(
    private val reportsRepository: ReportsRepositoryContract,
    private val shopsRepository: ShopsRepositoryContract,
    private val localAreaImageRepository: LocalImageRepositoryContract
): LoadThreeMonthsFullReportsUseCaseContract {
    override suspend operator fun invoke(): List<FullReport> {
        val reports = reportsRepository.load()
        
        // 今日の日付を取得
        val today = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault()).date
        
        // 3ヶ月前の日付を計算（今月を含めて3ヶ月）
        val threeMonthsAgo = today.minus(kotlinx.datetime.DatePeriod(months = 2))
        
        // 3ヶ月分のデータをフィルタリング
        val filteredReports = reports.filter { report ->
            report.date != null && report.date >= threeMonthsAgo
        }
        
        return filteredReports.map { report ->
            val shop = shopsRepository.getShopById(report.shopId)
            val photoData = try {
                localAreaImageRepository.load(report.photoName)
            } catch (e: Exception) {
                null
            }
            
            FullReport(
                id = report.id,
                shopId = report.shopId,
                shopName = shop?.name ?: "不明な店舗",
                menuName = report.menuName,
                photoName = report.photoName,
                imageBytes = photoData,
                impression = report.impression,
                date = report.date!!
            )
        }.sortedByDescending { it.date }
    }
}
