package dev.seabat.ramennote.data.repository

import dev.seabat.ramennote.data.datasource.ShopAiDataSourceContract
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.model.ShopAiInfo

class ShopAiRepository(
    private val shopAiDataSource: ShopAiDataSourceContract
) : ShopAiRepositoryContract {
    override suspend fun fetch(prompt: String): RunStatus<ShopAiInfo> =
        runCatching {
            shopAiDataSource.generate(prompt)
        }.fold(
            onSuccess = { shopAiInfo ->
                RunStatus.Success(shopAiInfo)
            },
            onFailure = {
                RunStatus.Error(it.message ?: "")
            }
        )
}
