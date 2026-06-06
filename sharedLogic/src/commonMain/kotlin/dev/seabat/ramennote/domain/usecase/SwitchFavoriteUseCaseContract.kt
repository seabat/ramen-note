package dev.seabat.ramennote.domain.usecase

interface SwitchFavoriteUseCaseContract {
    suspend operator fun invoke(onOff: Boolean, shopId: Int)
}
