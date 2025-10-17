package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.ShopsRepositoryContract
import dev.seabat.ramennote.domain.model.Shop

class AddShopUseCase(
    private val shopsRepository: ShopsRepositoryContract
) : AddShopUseCaseContract {
    
    override suspend operator fun invoke(shop: Shop) {
        // 全てのShopを読み込んでidの最大値を取得
        val allShops = shopsRepository.getAllShops()
        val maxId = allShops.maxOfOrNull { it.id } ?: 0
        
        // 引数で受け取ったShopのidに最大値+1をセット
        val shopWithId = shop.copy(id = maxId + 1)
        
        // SQLiteに追加
        shopsRepository.insertShop(shopWithId)
    }
}
