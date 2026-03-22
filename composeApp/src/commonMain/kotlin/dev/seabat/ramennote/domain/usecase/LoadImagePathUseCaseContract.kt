package dev.seabat.ramennote.domain.usecase

interface LoadImagePathUseCaseContract {
    suspend operator fun invoke(photoName: String): String?
}
