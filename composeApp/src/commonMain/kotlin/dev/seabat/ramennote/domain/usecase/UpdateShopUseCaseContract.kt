package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.domain.model.Shop

interface UpdateShopUseCaseContract {
    suspend fun updateShop(shop: Shop)
}
