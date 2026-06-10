package dev.seabat.ramennote.data.repository

import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.model.ShopAiInfo

interface ShopAiRepositoryContract {
    suspend fun fetch(prompt: String): RunStatus<ShopAiInfo>
}
