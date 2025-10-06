package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.domain.model.RunStatus

interface DeleteShopAndImageUseCaseContract {
    suspend fun invoke(shopId: Int): RunStatus<String>
}
