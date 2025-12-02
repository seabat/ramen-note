package dev.seabat.ramennote.data.datasource

import dev.seabat.ramennote.domain.model.ShopAiInfo

interface ShopAiDataSourceContract {
    suspend fun generate(prompt: String): ShopAiInfo
}
