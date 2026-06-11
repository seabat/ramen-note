package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.domain.model.RunStatus

interface UpdateAreaUseCaseContract {
    suspend operator fun invoke(areaId: Int, newName: String, byteArray: ByteArray? = null): RunStatus<String>
}
