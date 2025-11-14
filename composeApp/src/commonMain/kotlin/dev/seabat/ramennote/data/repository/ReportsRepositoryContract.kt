package dev.seabat.ramennote.data.repository

import dev.seabat.ramennote.domain.model.Report

interface ReportsRepositoryContract {
    suspend fun load(): List<Report>

    suspend fun insert(report: Report)

    suspend fun loadById(id: Int): Report?

    suspend fun update(report: Report)

    suspend fun delete(id: Int)
}
