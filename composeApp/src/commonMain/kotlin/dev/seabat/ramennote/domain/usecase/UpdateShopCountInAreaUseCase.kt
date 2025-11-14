package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.AreasRepositoryContract
import dev.seabat.ramennote.data.repository.ShopsRepositoryContract
import dev.seabat.ramennote.domain.util.createTodayLocalDate

class UpdateShopCountInAreaUseCase(
    private val areasRepository: AreasRepositoryContract,
    private val shopsRepository: ShopsRepositoryContract
) : UpdateShopCountInAreaUseCaseContract {
    override suspend operator fun invoke(area: String) {
        val shops = shopsRepository.getShopsByArea(area)
        val count = shops.size
        val existing = areasRepository.fetch(area) ?: return
        val nowDate = createTodayLocalDate()
        val updated = existing.copy(count = count, updatedDate = nowDate)
        areasRepository.edit(updated)
    }
}
