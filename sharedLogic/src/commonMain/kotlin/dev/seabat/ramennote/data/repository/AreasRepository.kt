package dev.seabat.ramennote.data.repository

import androidx.room.immediateTransaction
import androidx.room.useWriterConnection
import dev.seabat.ramennote.data.database.RamenNoteDatabase
import dev.seabat.ramennote.data.database.dao.AreaDao
import dev.seabat.ramennote.data.database.entity.AreaEntity
import dev.seabat.ramennote.domain.model.Area
import dev.seabat.ramennote.domain.model.RunStatus
import kotlinx.datetime.LocalDate

class AreasRepository(
    private val areaDao: AreaDao,
    private val database: RamenNoteDatabase
) : AreasRepositoryContract {
    override suspend fun load(): List<Area> {
        val entities = areaDao.getAllAreas()
        return entities.map { entity -> entity.toDomain() }
    }

    override suspend fun load(areaName: String): Area? {
        val entity = areaDao.getAreaByName(areaName) ?: return null
        return entity.toDomain()
    }

    override suspend fun loadByAreaId(areaId: Int): Area? {
        val entity = areaDao.getAreaById(areaId) ?: return null
        return entity.toDomain()
    }

    override suspend fun add(area: Area) {
        val maxSort = areaDao.getMaxSort()
        val entity =
            AreaEntity(
                name = area.name,
                count = area.count,
                date = area.updatedDate.toString(),
                sort = maxSort + 1
            )
        areaDao.insertArea(entity)
    }

    override suspend fun edit(oldName: String, newName: String): RunStatus<String> {
        val existingEntity = areaDao.getAreaByName(oldName)
        if (existingEntity != null) {
            // areaId が PK になったため、name のみ UPDATE すればよい
            val updatedEntity = existingEntity.copy(name = newName)
            areaDao.updateArea(updatedEntity)
            return RunStatus.Success(data = "")
        }
        return RunStatus.Error(errorMessage = "${oldName}は登録されていません。編集に失敗しました")
    }

    override suspend fun edit(area: Area): RunStatus<String> {
        val existingEntity = areaDao.getAreaByName(area.name)
        return if (existingEntity != null) {
            val updated =
                existingEntity.copy(
                    count = area.count,
                    date = area.updatedDate.toString(),
                    sort = area.sort
                )
            areaDao.updateArea(updated)
            RunStatus.Success("")
        } else {
            RunStatus.Error("${area.name}は登録されていません。編集に失敗しました")
        }
    }

    override suspend fun editAll(areas: List<Area>): RunStatus<String> =
        try {
            database.useWriterConnection { transactor ->
                transactor.immediateTransaction {
                    areas.forEach { area ->
                        val existingEntity = areaDao.getAreaByName(area.name)
                        if (existingEntity != null) {
                            val updated =
                                existingEntity.copy(
                                    count = area.count,
                                    date = area.updatedDate.toString(),
                                    sort = area.sort
                                )
                            areaDao.updateArea(updated)
                        }
                    }
                }
            }
            RunStatus.Success("")
        } catch (e: Exception) {
            RunStatus.Error("エリアの更新に失敗しました: ${e.message}")
        }

    override suspend fun delete(areaName: String): RunStatus<String> {
        val existingEntity = areaDao.getAreaByName(areaName)
        if (existingEntity != null) {
            areaDao.deleteArea(existingEntity)
            return RunStatus.Success(data = "")
        }
        return RunStatus.Error(errorMessage = "${areaName}は登録されていません。削除に失敗しました")
    }
}

private fun AreaEntity.toDomain(): Area =
    Area(
        areaId = areaId,
        name = name,
        updatedDate = LocalDate.parse(date),
        count = count,
        sort = sort
    )
