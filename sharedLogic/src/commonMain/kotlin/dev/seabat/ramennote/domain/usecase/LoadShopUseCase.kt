package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.ShopsRepositoryContract

class LoadShopUseCase(
    private val shopsRepository: ShopsRepositoryContract
) : LoadShopUseCaseContract {
    override suspend fun invoke(id: Int) = shopsRepository.getShopById(id)
}
