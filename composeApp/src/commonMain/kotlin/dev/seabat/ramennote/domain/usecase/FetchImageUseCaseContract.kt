package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.domain.model.RunStatus

interface FetchImageUseCaseContract {
    operator suspend fun invoke(): RunStatus<ByteArray?>
}