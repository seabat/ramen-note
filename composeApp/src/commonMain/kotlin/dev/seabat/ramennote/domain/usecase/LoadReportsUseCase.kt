package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.ReportsRepositoryContract
import dev.seabat.ramennote.data.repository.ShopsRepositoryContract
import dev.seabat.ramennote.data.repository.LocalAreaImageRepositoryContract
import dev.seabat.ramennote.domain.model.FullReport

class LoadReportsUseCase(
    private val reportsRepository: ReportsRepositoryContract,
    private val shopsRepository: ShopsRepositoryContract,
    private val localAreaImageRepository: LocalAreaImageRepositoryContract
): LoadReportsUseCaseContract {
    override suspend fun invoke(): List<FullReport> {
        val reports = reportsRepository.load()
        
        return reports.map { report ->
            val shop = shopsRepository.getShopById(report.shopId)
            val photoData = try {
                localAreaImageRepository.load(report.photoName)
            } catch (e: Exception) {
                null
            }
            
            FullReport(
                id = report.id,
                shopName = shop?.name ?: "不明な店舗",
                menuName = report.menuName,
                photoName = report.photoName,
                imageBytes = photoData,
                impression = report.impression,
                date = report.date!!
            )
        }
    }
}


