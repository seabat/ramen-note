package dev.seabat.ramennote.domain.usecase

interface DeleteScheduleInShopUseCaseContract {
    suspend operator fun invoke(shopId: Int)
}