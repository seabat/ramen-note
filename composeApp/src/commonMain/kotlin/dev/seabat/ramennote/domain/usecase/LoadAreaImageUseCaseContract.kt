package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.domain.model.RunStatus

interface LoadAreaImageUseCaseContract {
    suspend operator fun invoke(areaId: Int): RunStatus<ByteArray>
}
