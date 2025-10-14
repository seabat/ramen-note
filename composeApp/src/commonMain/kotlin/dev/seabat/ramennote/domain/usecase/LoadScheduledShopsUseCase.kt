package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.ShopsRepositoryContract
import dev.seabat.ramennote.domain.model.Shop
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class LoadScheduledShopsUseCase(
    private val shopsRepository: ShopsRepositoryContract
) : LoadScheduledShopsUseCaseContract {
    override suspend operator fun invoke(): List<Shop> {
        val allShops = shopsRepository.getAllShops()
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        return allShops
            .asSequence()
            .filter { it.scheduledDate != null && it.scheduledDate >= today }
            .sortedBy { it.scheduledDate }
            .toList()
    }
}


