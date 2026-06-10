package dev.seabat.ramennote.data.repository

import dev.seabat.ramennote.domain.model.RunStatus

interface GoogleMapSearchUrlRepositoryContract {
    suspend fun createUrl(areaName: String, shopName: String): RunStatus<String>
}
