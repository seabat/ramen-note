package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.domain.model.RunStatus

interface AddAreaUseCaseContract {
    suspend operator fun invoke(areaName: String): RunStatus<String>
}
