package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.ShopsRepositoryContract

class UpdateStarUseCase(
    private val shopsRepository: ShopsRepositoryContract
) : UpdateStarUseCaseContract {
    
    override suspend fun invoke(shopId: Int, star: Int) {
        // SQLiteから対象のShopデータを取得
        val shop = shopsRepository.getShopById(shopId)
        
        if (shop != null) {
            // Shopのstarを更新
            val updatedShop = shop.copy(star = star)
            
            // SQLiteを更新
            shopsRepository.updateShop(updatedShop)
        }
    }
}
