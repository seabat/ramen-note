package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.domain.model.Shop

interface LoadFavoriteShopsUseCaseContract {
    suspend operator fun invoke(): List<Shop>
}
