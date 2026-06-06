package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.domain.model.RunStatus

interface FetchAndSaveUnsplashImageUseCaseContract {
    suspend operator fun invoke(query: String): RunStatus<ByteArray>
}
