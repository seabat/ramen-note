package dev.seabat.ramennote.data.repository

import dev.seabat.ramennote.data.database.RamenNoteDatabase
import dev.seabat.ramennote.data.database.dao.ReportDao
import dev.seabat.ramennote.data.database.entity.ReportEntity
import dev.seabat.ramennote.domain.model.Report
import kotlinx.datetime.LocalDate

interface ReportsRepositoryContract {
    suspend fun load(): List<Report>
}

class ReportsRepository(
    private val database: RamenNoteDatabase
) : ReportsRepositoryContract {

    private val reportDao: ReportDao by lazy { database.reportDao() }

    override suspend fun load(): List<Report> {
        return reportDao.getAllReportsAsc().map { it.toDomain() }
    }
}

private fun ReportEntity.toDomain(): Report = Report(
    id = id,
    menuName = menuName,
    photoName = photoName,
    impression = impression,
    date = if (date.isEmpty()) null else LocalDate.parse(date)
)


