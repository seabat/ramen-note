package dev.seabat.ramennote.data.repository

import dev.seabat.ramennote.domain.model.Area
import dev.seabat.ramennote.domain.model.RunStatus

interface AreasRepositoryContract {
    suspend fun fetch(): List<Area>

    suspend fun fetch(areaName: String): Area?

    suspend fun add(area: Area)

    suspend fun edit(oldName: String, newName: String): RunStatus<String>

    suspend fun edit(area: Area): RunStatus<String>

    suspend fun delete(areaName: String): RunStatus<String>
}
