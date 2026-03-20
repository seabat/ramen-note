package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.LocalImageRepositoryContract
import dev.seabat.ramennote.data.repository.ReportsRepositoryContract
import dev.seabat.ramennote.data.repository.ShopsRepositoryContract
import dev.seabat.ramennote.domain.model.FullReport
import dev.seabat.ramennote.domain.model.RunStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LoadFullReportsUseCase(
    private val reportsRepository: ReportsRepositoryContract,
    private val shopsRepository: ShopsRepositoryContract,
    private val localImageRepository: LocalImageRepositoryContract
) : LoadFullReportsUseCaseContract {
    override operator fun invoke(): Flow<FullReport> =
        flow {
            val reports = reportsRepository.load()

            reports.forEach { report ->
                val shop = shopsRepository.getShopById(report.shopId)
                val imageBytes = localImageRepository.load(report.photoName)
                emit(
                    FullReport(
                        id = report.id,
                        shopId = report.shopId,
                        shopName = shop?.name ?: "不明な店舗",
                        menuName = report.menuName,
                        photoName = report.photoName,
                        imageBytes =
                            when (imageBytes) {
                                is RunStatus.Success ->
                                    imageBytes.data
                                is RunStatus.Error ->
                                    null
                                else -> error("unexpected state")
                            },
                        impression = report.impression,
                        date = report.date!!,
                        star = report.star,
                        areaId = report.areaId
                    )
                )
            }
        }
}
