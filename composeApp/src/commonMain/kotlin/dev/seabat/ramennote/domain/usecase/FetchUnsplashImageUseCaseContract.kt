package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.domain.model.RunStatus

interface FetchUnsplashImageUseCaseContract {
    suspend operator fun invoke(query: String): RunStatus<ByteArray?>
}
