package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.ShopsRepositoryContract
import dev.seabat.ramennote.domain.model.Shop

class UpdateShopUseCase(
    private val shopsRepository: ShopsRepositoryContract
) : UpdateShopUseCaseContract {
    override suspend fun updateShop(shop: Shop) {
        shopsRepository.updateShop(shop)
    }
}
