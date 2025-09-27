package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.domain.model.RunStatus

interface UpdateAreaUseCaseContract {
    suspend operator fun invoke(oldName: String, newName: String): RunStatus<String>
}