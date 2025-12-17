package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.LocalImageRepositoryContract
import dev.seabat.ramennote.data.repository.ShopsRepositoryContract
import dev.seabat.ramennote.domain.model.Shop

class UpdateShopUseCase(
    private val shopsRepository: ShopsRepositoryContract,
    private val localAreaImageRepository: LocalImageRepositoryContract,
    private val updateShopCountInAreaUseCase: UpdateShopCountInAreaUseCaseContract
) : UpdateShopUseCaseContract {
    override suspend operator fun invoke(shop: Shop, byteArray: ByteArray?, oldArea: String) {
        if (byteArray != null && shop.photoName1.isNotEmpty()) {
            localAreaImageRepository.save(byteArray, shop.photoName1)
        }
        shopsRepository.updateShop(shop)

        // エリアの変更が無くても店舗件数を更新
        updateShopCountInAreaUseCase(oldArea)
        updateShopCountInAreaUseCase(shop.area)
    }
}
