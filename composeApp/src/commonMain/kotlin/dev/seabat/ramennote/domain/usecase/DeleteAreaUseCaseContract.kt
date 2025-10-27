package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.domain.model.RunStatus

interface DeleteAreaUseCaseContract {
    suspend operator fun invoke(name: String): RunStatus<String>
}
