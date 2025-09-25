package dev.seabat.ramennote.data.repository

import androidx.room.immediateTransaction
import androidx.room.useWriterConnection
import dev.seabat.ramennote.data.database.RamenNoteDatabase
import dev.seabat.ramennote.data.database.dao.AreaDao
import dev.seabat.ramennote.data.database.entity.AreaEntity
import dev.seabat.ramennote.domain.model.Area
import dev.seabat.ramennote.domain.model.RunStatus
import kotlinx.datetime.LocalDate
import kotlin.lazy

class AreasRepository(
    private val database: RamenNoteDatabase
) : AreasRepositoryContract {

    private val areaDao: AreaDao by lazy {
        database.areaDao()
    }

    override suspend fun fetch(): List<Area> {
        val entities = areaDao.getAllAreas()
        return entities.map { entity ->
            Area(
                name = entity.name,
                updatedDate = LocalDate.parse(entity.date),
                count = entity.count
            )
        }
    }

    override suspend fun add(area: Area) {
        val entity = AreaEntity(
            name = area.name,
            count = area.count,
            date = area.updatedDate.toString()
        )
        areaDao.insertArea(entity)
    }

    override suspend fun edit(oldName: String, newName: String): RunStatus<String> {
        val existingEntity = areaDao.getAreaByName(oldName)
        if (existingEntity != null) {
            // 主キー（name）を変更する場合は、トランザクション内で削除と挿入を実行
            database.useWriterConnection { transactor ->
                transactor.immediateTransaction {
                    val updatedEntity = existingEntity.copy(name = newName)
                    areaDao.deleteArea(existingEntity)
                    areaDao.insertArea(updatedEntity)
                }
            }
            return RunStatus.Success(data = "")
        }
        return RunStatus.Error(errorMessage = "${oldName}は登録されていません。編集に失敗しました")
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