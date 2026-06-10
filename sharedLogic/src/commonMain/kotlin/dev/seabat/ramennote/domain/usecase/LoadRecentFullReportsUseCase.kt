package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.LocalImageRepositoryContract
import dev.seabat.ramennote.data.repository.ReportsRepositoryContract
import dev.seabat.ramennote.data.repository.ShopsRepositoryContract
import dev.seabat.ramennote.domain.model.FullReport

class LoadRecentFullReportsUseCase(
    private val reportsRepository: ReportsRepositoryContract,
    private val shopsRepository: ShopsRepositoryContract,
    private val localAreaImageRepository: LocalImageRepositoryContract
) : LoadRecentFullReportsUseCaseContract {
    override suspend operator fun invoke(): List<FullReport> {
        val reports = reportsRepository.load()

        // 日付の降順でソートし最新10件を取得
        val recentReports =
            reports
                .filter { report -> report.date != null }
                .sortedByDescending { it.date }
                .take(10)

        return recentReports.map { report ->
            val shop = shopsRepository.getShopById(report.shopId)
            val imagePath = localAreaImageRepository.getFilePath(report.photoName)

            FullReport(
                id = report.id,
                shopId = report.shopId,
                shopName = shop?.name ?: "不明な店舗",
                menuName = report.menuName,
                photoName = report.photoName,
                imagePath = imagePath,
                impression = report.impression,
                date = report.date!!,
                star = report.star,
                areaId = report.areaId
            )
        }
    }
}
