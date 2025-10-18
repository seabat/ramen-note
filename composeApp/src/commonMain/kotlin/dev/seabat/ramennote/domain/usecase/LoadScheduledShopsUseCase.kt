package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.ReportsRepositoryContract
import dev.seabat.ramennote.data.repository.ShopsRepositoryContract
import dev.seabat.ramennote.domain.model.Schedule
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class LoadScheduledShopsUseCase(
    private val shopsRepository: ShopsRepositoryContract,
    private val reportsRepository: ReportsRepositoryContract
) : LoadScheduledShopsUseCaseContract {
    override suspend operator fun invoke(): List<Schedule> {
        val allShops = shopsRepository.getAllShops()
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        
        // scheduledDate が今日を含め未来の日付のみにフィルタリング
        val filteredShops = allShops
            .asSequence()
            .filter { it.scheduledDate != null && it.scheduledDate >= today }
            .sortedBy { it.scheduledDate }
            .toList()
        
        // List<Shop> を List<Schedule> に変換
        val schedules = filteredShops.map { Schedule.fromShop(it) }
        
        // ReportsRepositoryContract.load() で Report 全件を読み出す
        val reports = reportsRepository.load()
        
        // List<Schedule> と Report 全件を比較し、shop の id と scheduledDate が一致する場合は、Schedule.isReported を true に更新
        return schedules.map { schedule ->
            val hasReport = reports.any { report ->
                report.shopId == schedule.shopId && report.date == schedule.scheduledDate
            }
            schedule.copy(isReported = hasReport)
        }
    }
}


