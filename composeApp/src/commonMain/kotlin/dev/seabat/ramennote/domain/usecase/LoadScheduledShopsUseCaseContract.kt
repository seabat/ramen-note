package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.domain.model.Shop

interface LoadScheduledShopsUseCaseContract {
    suspend operator fun invoke(): List<Shop>
}


