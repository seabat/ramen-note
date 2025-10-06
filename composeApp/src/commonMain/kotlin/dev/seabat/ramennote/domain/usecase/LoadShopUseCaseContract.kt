package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.domain.model.Shop

interface LoadShopUseCaseContract {
    suspend fun invoke(id: Int): Shop?
}
