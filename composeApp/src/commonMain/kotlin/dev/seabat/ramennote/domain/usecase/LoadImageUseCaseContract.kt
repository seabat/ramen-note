package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.domain.model.RunStatus

interface LoadImageUseCaseContract {
    suspend operator fun invoke(name: String): RunStatus<ByteArray?>
}