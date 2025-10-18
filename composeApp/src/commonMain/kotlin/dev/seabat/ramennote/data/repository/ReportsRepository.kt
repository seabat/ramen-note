package dev.seabat.ramennote.data.repository

import dev.seabat.ramennote.data.database.RamenNoteDatabase
import dev.seabat.ramennote.data.database.dao.ReportDao
import dev.seabat.ramennote.data.database.entity.ReportEntity
import dev.seabat.ramennote.domain.model.Report
import kotlinx.datetime.LocalDate

class ReportsRepository(
    private val database: RamenNoteDatabase
) : ReportsRepositoryContract {

    private val reportDao: ReportDao by lazy { database.reportDao() }

    override suspend fun load(): List<Report> {
        return reportDao.getAllReportsDesc().map { it.toDomain() }
    }
    
    override suspend fun insert(report: Report) {
        reportDao.insert(report.toEntity())
    }
    override suspend fun loadById(id: Int): Report? {
        return reportDao.getReportById(id)?.toDomain()
    }
    
    override suspend fun update(report: Report) {
        reportDao.update(report.toEntity())
    }
    
    override suspend fun delete(id: Int) {
        reportDao.deleteById(id)
    }
}

private fun ReportEntity.toDomain(): Report = Report(
    id = id,
    shopId = shopId,
    menuName = menuName,
    photoName = photoName,
    impression = impression,
    date = if (date.isEmpty()) null else LocalDate.parse(date),
    star = star
)

private fun Report.toEntity(): ReportEntity = ReportEntity(
    id = id,
    shopId = shopId,
    menuName = menuName,
    photoName = photoName,
    impression = impression,
    date = date?.toString() ?: "",
    star = star
)


