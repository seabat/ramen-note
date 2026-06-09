package dev.seabat.ramennote.data.repository

import dev.seabat.ramennote.domain.model.RunStatus

interface ExpandShortUrlRepositoryContract {
    suspend fun expand(shortUrl: String): RunStatus<String>
}
