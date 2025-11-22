package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.model.ShopAiInfo

interface FetchAiShopUseCaseContract {
    suspend operator fun invoke(shopName: String) : RunStatus<ShopAiInfo>
}