package dev.seabat.ramennote.domain.usecase

interface CreateNoImageIfNeededUseCaseContract {
    suspend operator fun invoke(fileName: String)
}