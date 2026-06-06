package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.LocalImageRepositoryContract
import dev.seabat.ramennote.data.repository.ReportsRepositoryContract
import dev.seabat.ramennote.data.repository.ShopsRepositoryContract
import dev.seabat.ramennote.domain.model.RunStatus

class DeleteShopAndImageUseCase(
    private val shopsRepository: ShopsRepositoryContract,
    private val localAreaImageRepository: LocalImageRepositoryContract,
    private val updateShopCountInAreaUseCase: UpdateShopCountInAreaUseCaseContract,
    private val reportsRepository: ReportsRepositoryContract
) : DeleteShopAndImageUseCaseContract {
    override suspend operator fun invoke(shopId: Int): RunStatus<String> =
        try {
            // Shopデータを取得して画像名とエリアIDを確認（削除前に取得する）
            val shop = shopsRepository.getShopById(shopId)
            if (shop != null) {
                // 関連するレポートを削除
                deleteReportsByShopId(shopId)

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

                // 削除したshopのエリアIDでエリア件数を更新する
                if (shop.areaId != 0) {
                    updateShopCountInAreaUseCase(shop.areaId)
                }
            }
            RunStatus.Success(data = "削除が完了しました")
        } catch (e: Exception) {
            RunStatus.Error(errorMessage = "削除に失敗しました: ${e.message}")
        }

    private suspend fun deleteReportsByShopId(shopId: Int) {
        reportsRepository.deleteByShopId(shopId)
    }
}
