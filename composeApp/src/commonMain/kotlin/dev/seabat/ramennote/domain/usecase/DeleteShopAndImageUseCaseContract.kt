package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.domain.model.RunStatus

interface DeleteShopAndImageUseCaseContract {
    suspend operator fun invoke(shopId: Int): RunStatus<String>
}
