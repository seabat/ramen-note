package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.ShopsRepositoryContract
import dev.seabat.ramennote.data.repository.ReportsRepositoryContract
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.model.Schedule
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class LoadRecentScheduleUseCase(
    private val shopsRepository: ShopsRepositoryContract,
    private val reportsRepository: ReportsRepositoryContract
) : LoadRecentScheduleUseCaseContract {
    override suspend operator fun invoke(): RunStatus<Schedule?> {
        return try {
            val allShops = shopsRepository.getAllShops()
            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            
            // scheduledDateが今日以降のShopをフィルタリング
            val futureShops = allShops.filter { shop ->
                shop.scheduledDate != null && shop.scheduledDate >= today
            }
            
            // scheduledDateでソート（最も近い日付順）して最初の1件を返す
            val recentShop = futureShops.sortedBy { it.scheduledDate }.firstOrNull()
            if (recentShop != null) {
                // ShopをScheduleに変換
                val recentSchedule = Schedule.fromShop(recentShop)
                
                // ReportsRepositoryContract.oad()でList<Report>を読み出す
                val reports = reportsRepository.load()
                
                // recentSchedule の shopId と scheduledDate が List<Report> に含まれていれば isReported を true にする
                val isReported = reports.any { report ->
                    report.shopId == recentSchedule.shopId && 
                    report.date == recentSchedule.scheduledDate
                }
                
                val scheduleWithReportedStatus = recentSchedule.copy(isReported = isReported)
                RunStatus.Success(scheduleWithReportedStatus)
            } else {
                RunStatus.Error("予定が見つかりませんでした")
            }
        } catch (e: Exception) {
            RunStatus.Error("予定の読み込みに失敗しました")
        }
    }
}
