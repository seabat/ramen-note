package dev.seabat.ramennote.domain.usecase

interface UpdateStarUseCaseContract {
    suspend operator fun invoke(shopId: Int, star: Int)
}
