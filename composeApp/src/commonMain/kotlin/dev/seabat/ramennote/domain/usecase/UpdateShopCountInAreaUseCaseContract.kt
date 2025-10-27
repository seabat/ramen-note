package dev.seabat.ramennote.domain.usecase

interface UpdateShopCountInAreaUseCaseContract {
    suspend operator fun invoke(area: String)
}
