package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.LocalImageRepositoryContract
import dev.seabat.ramennote.data.repository.ShopsRepositoryContract
import dev.seabat.ramennote.domain.model.RunStatus

class DeleteShopAndImageUseCase(
    private val shopsRepository: ShopsRepositoryContract,
    private val localAreaImageRepository: LocalImageRepositoryContract,
    private val updateShopCountInAreaUseCase: UpdateShopCountInAreaUseCaseContract
) : DeleteShopAndImageUseCaseContract {
    override suspend operator fun invoke(shopId: Int): RunStatus<String> =
        try {
            // Shopデータを取得して画像名を確認
            val shop = shopsRepository.getShopById(shopId)
            if (shop != null) {
                // Shopデータを削除
                shopsRepository.deleteShopById(shopId)

                // 画像ファイルを削除
                if (shop.photoName1.isNotEmpty()) {
                    localAreaImageRepository.delete(shop.photoName1)
                }
                if (shop.photoName2.isNotEmpty()) {
                    localAreaImageRepository.delete(shop.photoName2)
                }
                if (shop.photoName3.isNotEmpty()) {
                    localAreaImageRepository.delete(shop.photoName3)
                }
            }
            updateShopCount(shopId)
            RunStatus.Success(data = "削除が完了しました")
        } catch (e: Exception) {
            RunStatus.Error(errorMessage = "削除に失敗しました: ${e.message}")
        }

    private suspend fun updateShopCount(shopId: Int) {
        // 削除前にエリア名を取得
        val shop = shopsRepository.getShopById(shopId)
        val areaName = shop?.area

        // 削除成功時にエリア件数を更新する
        if (!areaName.isNullOrEmpty()) {
            updateShopCountInAreaUseCase(areaName)
        }
    }
}
