package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.AreasRepositoryContract
import dev.seabat.ramennote.data.repository.ShopsRepositoryContract
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class UpdateShopCountInAreaUseCase(
    private val areasRepository: AreasRepositoryContract,
    private val shopsRepository: ShopsRepositoryContract
) : UpdateShopCountInAreaUseCaseContract {
    override suspend operator fun invoke(area: String) {
        val shops = shopsRepository.getShopsByArea(area)
        val count = shops.size
        val existing = areasRepository.fetch(area) ?: return
        val nowDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val updated = existing.copy(count = count, updatedDate = nowDate)
        areasRepository.edit(updated)
    }
}


