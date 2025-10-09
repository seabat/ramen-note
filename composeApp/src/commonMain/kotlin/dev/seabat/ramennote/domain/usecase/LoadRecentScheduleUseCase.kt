package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.ShopsRepositoryContract
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.model.Shop
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class LoadRecentScheduleUseCase(
    private val shopsRepository: ShopsRepositoryContract
) : LoadRecentScheduleUseCaseContract {
    override suspend operator fun invoke(): RunStatus<Shop?> {
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
                RunStatus.Success(recentShop)
            } else {
                RunStatus.Error("予定が見つかりませんでした")
            }
        } catch (e: Exception) {
            RunStatus.Error("予定の読み込みに失敗しました")
        }
    }
}
