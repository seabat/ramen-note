package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.domain.model.Shop

interface AddShopUseCaseContract {
    suspend operator fun invoke(shop: Shop)
}
