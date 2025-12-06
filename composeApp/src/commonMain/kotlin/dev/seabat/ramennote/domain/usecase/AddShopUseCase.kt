package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.LocalImageRepositoryContract
import dev.seabat.ramennote.data.repository.ShopsRepositoryContract
import dev.seabat.ramennote.domain.model.Shop

class AddShopUseCase(
    private val shopsRepository: ShopsRepositoryContract,
    private val localAreaImageRepository: LocalImageRepositoryContract,
    private val updateShopCountInAreaUseCase: UpdateShopCountInAreaUseCaseContract
) : AddShopUseCaseContract {
    override suspend operator fun invoke(shop: Shop, byteArray: ByteArray?) {
        // 画像を保存
        if (byteArray != null && shop.photoName1.isNotEmpty()) {
            localAreaImageRepository.save(byteArray, shop.photoName1)
        }

        // 全てのShopを読み込んでidの最大値を取得
        val allShops = shopsRepository.getAllShops()
        val maxId = allShops.maxOfOrNull { it.id } ?: 0
        // 引数で受け取ったShopのidに最大値+1をセット
        val shopWithId = shop.copy(id = maxId + 1)
        // SQLiteに追加
        shopsRepository.insertShop(shopWithId)

        // エリア件数更新
        updateShopCountInAreaUseCase(shop.area)
    }
}
