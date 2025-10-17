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
    override suspend operator fun invoke(name: String): RunStatus<String> {
        val result = areasRepository.delete(name)
        return if (result is RunStatus.Success) {
            try {
                // エリア画像の削除
                localAreaImageRepository.delete(name)

                // 該当エリアのShopを削除
                val shops = shopsRepository.getShopsByArea(name)
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