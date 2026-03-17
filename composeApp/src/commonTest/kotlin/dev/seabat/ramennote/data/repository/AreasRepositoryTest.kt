package dev.seabat.ramennote.data.repository

import androidx.room.InvalidationTracker
import dev.seabat.ramennote.data.database.RamenNoteDatabase
import dev.seabat.ramennote.data.database.dao.AreaDao
import dev.seabat.ramennote.data.database.entity.AreaEntity
import dev.seabat.ramennote.domain.model.Area
import dev.seabat.ramennote.domain.model.RunStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AreasRepositoryTest {

    // --- load() ---

    @Test
    fun `load - DAOが返したエンティティをドメインモデルに変換して返す`() = runTest {
        val entities = listOf(
            AreaEntity(areaId = 1, name = "東京", count = 3, date = "2024-09-01", sort = 1),
            AreaEntity(areaId = 2, name = "大阪", count = 1, date = "2024-08-15", sort = 2)
        )
        val dao = FakeAreaDao(areas = entities.toMutableList())
        val repository = buildRepository(dao)

        val result = repository.load()

        assertEquals(2, result.size)
        assertEquals(1, result[0].areaId)
        assertEquals("東京", result[0].name)
        assertEquals(3, result[0].count)
        assertEquals(LocalDate.parse("2024-09-01"), result[0].updatedDate)
        assertEquals(1, result[0].sort)
        assertEquals(2, result[1].areaId)
        assertEquals("大阪", result[1].name)
    }

    @Test
    fun `load - DAOがエンティティを返さない場合は空リストを返す`() = runTest {
        val dao = FakeAreaDao()
        val repository = buildRepository(dao)

        val result = repository.load()

        assertTrue(result.isEmpty())
    }

    // --- load(areaName) ---

    @Test
    fun `load areaName - 一致するエリアをドメインモデルに変換して返す`() = runTest {
        val entity = AreaEntity(areaId = 1, name = "東京", count = 5, date = "2024-09-01", sort = 1)
        val dao = FakeAreaDao(areas = mutableListOf(entity))
        val repository = buildRepository(dao)

        val result = repository.load("東京")

        assertEquals(1, result?.areaId)
        assertEquals("東京", result?.name)
        assertEquals(5, result?.count)
    }

    @Test
    fun `load areaName - 一致するエリアがない場合はnullを返す`() = runTest {
        val dao = FakeAreaDao()
        val repository = buildRepository(dao)

        val result = repository.load("存在しないエリア")

        assertNull(result)
    }

    // --- loadByAreaId ---

    @Test
    fun `loadByAreaId - 一致するエリアをドメインモデルに変換して返す`() = runTest {
        val entity = AreaEntity(areaId = 42, name = "福岡", count = 2, date = "2024-07-10", sort = 3)
        val dao = FakeAreaDao(areas = mutableListOf(entity))
        val repository = buildRepository(dao)

        val result = repository.loadByAreaId(42)

        assertEquals(42, result?.areaId)
        assertEquals("福岡", result?.name)
    }

    @Test
    fun `loadByAreaId - 一致するIDがない場合はnullを返す`() = runTest {
        val dao = FakeAreaDao()
        val repository = buildRepository(dao)

        val result = repository.loadByAreaId(999)

        assertNull(result)
    }

    // --- add ---

    @Test
    fun `add - maxSort + 1 のsortでエンティティが挿入される`() = runTest {
        val existing = AreaEntity(areaId = 1, name = "東京", count = 0, date = "2024-01-01", sort = 3)
        val dao = FakeAreaDao(areas = mutableListOf(existing))
        val repository = buildRepository(dao)

        repository.add(
            Area(name = "大阪", count = 0, updatedDate = LocalDate.parse("2024-09-01"), sort = 0)
        )

        // sort は maxSort(3) + 1 = 4 になる
        val inserted = dao.insertedArea
        assertEquals("大阪", inserted?.name)
        assertEquals(4, inserted?.sort)
        assertEquals(0, inserted?.count)
        assertEquals("2024-09-01", inserted?.date)
    }

    @Test
    fun `add - エリアがひとつも登録されていない場合はsort=1で挿入される`() = runTest {
        val dao = FakeAreaDao() // getMaxSort() は 0 を返す
        val repository = buildRepository(dao)

        repository.add(
            Area(name = "北海道", count = 0, updatedDate = LocalDate.parse("2024-09-01"), sort = 0)
        )

        assertEquals(1, dao.insertedArea?.sort)
    }

    // --- edit(oldName, newName) ---

    @Test
    fun `edit oldName newName - 既存エリアの名前を更新してSuccessを返す`() = runTest {
        val entity = AreaEntity(areaId = 1, name = "旧名称", count = 2, date = "2024-01-01", sort = 1)
        val dao = FakeAreaDao(areas = mutableListOf(entity))
        val repository = buildRepository(dao)

        val result = repository.edit("旧名称", "新名称")

        assertIs<RunStatus.Success<String>>(result)
        assertEquals("新名称", dao.updatedArea?.name)
        // areaId はそのまま維持される
        assertEquals(1, dao.updatedArea?.areaId)
    }

    @Test
    fun `edit oldName newName - 存在しないエリア名の場合はErrorを返す`() = runTest {
        val dao = FakeAreaDao()
        val repository = buildRepository(dao)

        val result = repository.edit("存在しない", "新名称")

        assertIs<RunStatus.Error<String>>(result)
        assertTrue(result.message?.contains("存在しない") == true)
    }

    // --- edit(area) ---

    @Test
    fun `edit area - 既存エリアのcount・date・sortを更新してSuccessを返す`() = runTest {
        val entity = AreaEntity(areaId = 1, name = "東京", count = 0, date = "2024-01-01", sort = 1)
        val dao = FakeAreaDao(areas = mutableListOf(entity))
        val repository = buildRepository(dao)

        val result = repository.edit(
            Area(areaId = 1, name = "東京", count = 5, updatedDate = LocalDate.parse("2025-03-01"), sort = 2)
        )

        assertIs<RunStatus.Success<String>>(result)
        assertEquals(5, dao.updatedArea?.count)
        assertEquals("2025-03-01", dao.updatedArea?.date)
        assertEquals(2, dao.updatedArea?.sort)
        // name と areaId はそのまま維持
        assertEquals("東京", dao.updatedArea?.name)
        assertEquals(1, dao.updatedArea?.areaId)
    }

    @Test
    fun `edit area - 存在しないエリア名の場合はErrorを返す`() = runTest {
        val dao = FakeAreaDao()
        val repository = buildRepository(dao)

        val result = repository.edit(
            Area(name = "存在しない", count = 0, updatedDate = LocalDate.parse("2024-01-01"), sort = 1)
        )

        assertIs<RunStatus.Error<String>>(result)
        assertTrue(result.message?.contains("存在しない") == true)
    }

    // --- delete ---

    @Test
    fun `delete - 既存エリアを削除してSuccessを返す`() = runTest {
        val entity = AreaEntity(areaId = 1, name = "東京", count = 0, date = "2024-01-01", sort = 1)
        val dao = FakeAreaDao(areas = mutableListOf(entity))
        val repository = buildRepository(dao)

        val result = repository.delete("東京")

        assertIs<RunStatus.Success<String>>(result)
        assertEquals(entity, dao.deletedArea)
    }

    @Test
    fun `delete - 存在しないエリア名の場合はErrorを返す`() = runTest {
        val dao = FakeAreaDao()
        val repository = buildRepository(dao)

        val result = repository.delete("存在しない")

        assertIs<RunStatus.Error<String>>(result)
        assertTrue(result.message?.contains("存在しない") == true)
    }

    // --- ヘルパー ---

    private fun buildRepository(dao: FakeAreaDao): AreasRepository =
        AreasRepository(areaDao = dao, database = FakeRamenNoteDatabase(dao))

    // --- フェイク実装 ---

    private class FakeAreaDao(
        private val areas: MutableList<AreaEntity> = mutableListOf()
    ) : AreaDao {
        var insertedArea: AreaEntity? = null
        var updatedArea: AreaEntity? = null
        var deletedArea: AreaEntity? = null

        override suspend fun getAllAreas(): List<AreaEntity> = areas

        override fun getAllAreasFlow(): Flow<List<AreaEntity>> = flowOf(areas)

        override suspend fun getAreaByName(name: String): AreaEntity? =
            areas.find { it.name == name }

        override suspend fun getAreaById(areaId: Int): AreaEntity? =
            areas.find { it.areaId == areaId }

        override suspend fun insertArea(area: AreaEntity) {
            insertedArea = area
            areas.add(area)
        }

        override suspend fun updateArea(area: AreaEntity) {
            updatedArea = area
            val index = areas.indexOfFirst { it.areaId == area.areaId }
            if (index >= 0) areas[index] = area
        }

        override suspend fun deleteArea(area: AreaEntity) {
            deletedArea = area
            areas.removeAll { it.areaId == area.areaId }
        }

        override suspend fun deleteAreaByName(name: String) {
            areas.removeAll { it.name == name }
        }

        override suspend fun getMaxSort(): Int = areas.maxOfOrNull { it.sort } ?: 0
    }

    private class FakeRamenNoteDatabase(private val dao: FakeAreaDao) : RamenNoteDatabase() {
        override fun areaDao() = dao
        override fun shopDao() = throw UnsupportedOperationException("テスト対象外")
        override fun reportDao() = throw UnsupportedOperationException("テスト対象外")
        override fun createInvalidationTracker(): InvalidationTracker =
            InvalidationTracker(this, emptyMap(), emptyMap())
        override fun clearAllTables() = Unit
    }
}
