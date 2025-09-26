package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.domain.model.RunStatus

interface LoadImageListUseCaseContract {
    suspend operator fun invoke(): RunStatus<List<ByteArray>>
}