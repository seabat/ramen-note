package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.AreasRepositoryContract
import dev.seabat.ramennote.data.repository.LocalImageRepositoryContract
import dev.seabat.ramennote.data.repository.ShopsRepositoryContract
import dev.seabat.ramennote.domain.model.RunStatus

class DeleteAreaUseCase(
    private val areasRepository: AreasRepositoryContract,
    private val localAreaImageRepository: LocalImageRepositoryContract,
    private val shopsRepository: ShopsRepositoryContract
) : DeleteAreaUseCaseContract {
    override suspend operator fun invoke(areaId: Int): RunStatus<String> {
        // 削除前に エリア名を取得（削除後は loadByAreaId() が null を返すため）
        val name =
            areasRepository.loadByAreaId(areaId)?.name
                ?: return RunStatus.Error("エリアが見つかりません")

        val result = areasRepository.delete(name)
        return if (result is RunStatus.Success) {
            try {
                // エリア画像の削除
                localAreaImageRepository.delete(name)

                // 該当エリアのShopを削除
                val shops = shopsRepository.getShopsByAreaId(areaId)
                shops.forEach { shop ->
                    shopsRepository.deleteShopById(shop.id)
                }
                RunStatus.Success("")
            } catch (e: Exception) {
                RunStatus.Error(e.message ?: "エリア削除後の関連データ削除に失敗しました")
            }
        } else {
            result
        }
    }
}
