package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.domain.model.RunStatus

interface FetchPlaceHolderImageUseCaseContract {
    suspend operator fun invoke(): RunStatus<ByteArray?>
}
