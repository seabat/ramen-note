package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.LocalImageRepositoryContract
import dev.seabat.ramennote.data.repository.ReportsRepositoryContract
import dev.seabat.ramennote.data.repository.ShopsRepositoryContract
import dev.seabat.ramennote.domain.model.FullReport
import dev.seabat.ramennote.domain.model.RunStatus

class LoadFullReportUseCase(
    private val reportsRepository: ReportsRepositoryContract,
    private val shopsRepository: ShopsRepositoryContract,
    private val localAreaImageRepository: LocalImageRepositoryContract
) : LoadFullReportUseCaseContract {
    override suspend operator fun invoke(reportId: Int): FullReport? {
        val report = reportsRepository.loadById(reportId) ?: return null
        val shop = shopsRepository.getShopById(report.shopId)
        val imageBytes = localAreaImageRepository.load(report.photoName)

        return FullReport(
            id = report.id,
            shopId = report.shopId,
            shopName = shop?.name ?: "不明な店舗",
            menuName = report.menuName,
            photoName = report.photoName,
            imageBytes =
                when (imageBytes) {
                    is RunStatus.Success -> imageBytes.data
                    is RunStatus.Error -> return null
                    else -> error("unexpected state")
                },
            impression = report.impression,
            date = requireNotNull(report.date) { "report.date is null (id=${report.id})" },
            star = report.star,
            areaId = report.areaId
        )
    }
}
