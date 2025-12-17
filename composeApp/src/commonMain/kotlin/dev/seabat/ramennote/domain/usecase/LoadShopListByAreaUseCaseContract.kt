package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.domain.model.Shop

interface LoadShopListByAreaUseCaseContract {
    suspend operator fun invoke(area: String): List<Shop>
}
