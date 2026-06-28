package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.LocalImageRepositoryContract
import dev.seabat.ramennote.data.repository.ReportsRepositoryContract
import dev.seabat.ramennote.data.repository.ShopsRepositoryContract
import dev.seabat.ramennote.domain.model.FullReport
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LoadReportsByYearUseCase(
    private val reportsRepository: ReportsRepositoryContract,
    private val shopsRepository: ShopsRepositoryContract,
    private val localImageRepository: LocalImageRepositoryContract
) : LoadReportsByYearUseCaseContract {
    override operator fun invoke(year: Int): Flow<FullReport> =
        flow {
            val reports = reportsRepository.load().filter { it.date?.year == year }

            reports.forEach { report ->
                val shop = shopsRepository.getShopById(report.shopId)
                val imagePath = localImageRepository.getFilePath(report.photoName)
                emit(
                    FullReport(
                        id = report.id,
                        shopId = report.shopId,
                        shopName = shop?.name ?: "不明な店舗",
                        menuName = report.menuName,
                        photoName = report.photoName,
                        imagePath = imagePath,
                        impression = report.impression,
                        date = requireNotNull(report.date) { "report.date is null (id=${report.id})" },
                        star = report.star,
                        areaId = report.areaId
                    )
                )
            }
        }
}
