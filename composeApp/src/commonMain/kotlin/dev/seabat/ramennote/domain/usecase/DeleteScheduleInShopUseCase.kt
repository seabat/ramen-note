package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.ShopsRepositoryContract

class DeleteScheduleInShopUseCase(
    private val shopsRepository: ShopsRepositoryContract
) : DeleteScheduleInShopUseCaseContract {
    override suspend operator fun invoke(shopId: Int) {
        val shop = shopsRepository.getShopById(shopId) ?: return
        val updated = shop.copy(scheduledDate = null)
        shopsRepository.updateShop(updated)
    }
}