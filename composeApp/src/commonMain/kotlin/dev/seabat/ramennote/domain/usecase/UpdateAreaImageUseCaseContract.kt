package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.domain.model.RunStatus

interface UpdateAreaImageUseCaseContract {
    suspend operator fun invoke(area: String): RunStatus<ByteArray?>
}
