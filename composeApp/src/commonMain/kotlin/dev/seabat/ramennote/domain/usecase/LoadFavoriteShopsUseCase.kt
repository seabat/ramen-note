package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.ShopsRepositoryContract
import dev.seabat.ramennote.domain.model.Shop

class LoadFavoriteShopsUseCase(
    private val shopsRepository: ShopsRepositoryContract
) : LoadFavoriteShopsUseCaseContract {
    
    override suspend operator fun invoke(): List<Shop> {
        return shopsRepository.getAllShops().filter { it.favorite }
    }
}
