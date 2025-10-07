package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.domain.model.RunStatus

interface FetchPlaceHolderImageUseCaseContract {
    operator suspend fun invoke(): RunStatus<ByteArray?>
}