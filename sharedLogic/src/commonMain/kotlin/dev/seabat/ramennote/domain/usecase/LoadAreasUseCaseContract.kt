package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.domain.model.Area

interface LoadAreasUseCaseContract {
    suspend operator fun invoke(): List<Area>
}
