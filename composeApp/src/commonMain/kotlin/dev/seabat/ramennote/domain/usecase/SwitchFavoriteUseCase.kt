package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.ShopsRepositoryContract

class SwitchFavoriteUseCase(
    private val shopsRepository: ShopsRepositoryContract
) : SwitchFavoriteUseCaseContract {
    
    override suspend operator fun invoke(onOff: Boolean, shopId: Int) {
        val shop = shopsRepository.getShopById(shopId) ?: return
        val updatedShop = shop.copy(favorite = onOff)
        shopsRepository.updateShop(updatedShop)
    }
}
