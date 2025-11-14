package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.LocalImageRepositoryContract
import dev.seabat.ramennote.data.repository.ReportsRepositoryContract
import dev.seabat.ramennote.data.repository.ShopsRepositoryContract
import dev.seabat.ramennote.domain.model.FullReport

class LoadFullReportUseCase(
    private val reportsRepository: ReportsRepositoryContract,
    private val shopsRepository: ShopsRepositoryContract,
    private val localAreaImageRepository: LocalImageRepositoryContract
) : LoadFullReportUseCaseContract {
    override suspend operator fun invoke(reportId: Int): FullReport? {
        val report = reportsRepository.loadById(reportId) ?: return null
        val shop = shopsRepository.getShopById(report.shopId)
        val imageBytes =
            try {
                localAreaImageRepository.load(report.photoName)
            } catch (e: Exception) {
                null
            }
        return FullReport(
            id = report.id,
            shopId = report.shopId,
            shopName = shop?.name ?: "不明な店舗",
            menuName = report.menuName,
            photoName = report.photoName,
            imageBytes = imageBytes,
            impression = report.impression,
            date = report.date!!,
            star = report.star
        )
    }
}
