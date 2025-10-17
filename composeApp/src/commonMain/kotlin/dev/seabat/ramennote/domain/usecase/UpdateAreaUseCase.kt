package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.AreasRepositoryContract
import dev.seabat.ramennote.data.repository.LocalImageRepositoryContract
import dev.seabat.ramennote.data.repository.ShopsRepositoryContract
import dev.seabat.ramennote.domain.model.RunStatus

class UpdateAreaUseCase(
    private val areasRepository: AreasRepositoryContract,
    private val localAreaImageRepository: LocalImageRepositoryContract,
    private val shopsRepository: ShopsRepositoryContract
) : UpdateAreaUseCaseContract {

    override suspend fun invoke(oldName: String, newName: String): RunStatus<String> {
        val result = areasRepository.edit(oldName, newName)
        return if (result is RunStatus.Success) {
            try {
                // 画像名のリネーム
                localAreaImageRepository.rename(oldName, newName)

                // 該当エリアのShopを一括更新
                val shops = shopsRepository.getShopsByArea(oldName)
                shops.forEach { shop ->
                    val updated = shop.copy(area = newName)
                    shopsRepository.updateShop(updated)
                }
                RunStatus.Success("")
            } catch (e: Exception) {
                RunStatus.Error("${e.message}")
            }
        } else {
            result
        }
    }
}