package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.ShopsRepositoryContract
import dev.seabat.ramennote.data.repository.LocalAreaImageRepositoryContract
import dev.seabat.ramennote.domain.model.RunStatus

class DeleteShopAndImageUseCase(
    private val shopsRepository: ShopsRepositoryContract,
    private val localAreaImageRepository: LocalAreaImageRepositoryContract
) : DeleteShopAndImageUseCaseContract {
    
    override suspend operator fun invoke(shopId: Int): RunStatus<String> {
        return try {
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
            RunStatus.Success(data = "削除が完了しました")
        } catch (e: Exception) {
            RunStatus.Error(errorMessage = "削除に失敗しました: ${e.message}")
        }
    }
}
