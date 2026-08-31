package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.AreasRepositoryContract
import dev.seabat.ramennote.domain.model.Area
import dev.seabat.ramennote.domain.model.RunStatus
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class AddAreaUseCaseTest {

    private val fakeAreasRepository = FakeAreasRepositoryForAdd()

    private val useCase = AddAreaUseCase(areasRepository = fakeAreasRepository)

    // --- トリム ---

    @Test
    fun `invoke - エリア名の前後スペースがトリムされて登録される`() = runTest {
        useCase("  福岡  ")

        assertEquals("福岡", fakeAreasRepository.addedArea?.name)
    }

    @Test
    fun `invoke - 重複チェックもトリム後の名前で行われる`() = runTest {
        useCase("  福岡  ")

        assertEquals("福岡", fakeAreasRepository.loadedName)
    }

    // --- 重複 ---

    @Test
    fun `invoke - 同名のエリアが既にある場合はErrorを返す`() = runTest {
        fakeAreasRepository.existingArea = createArea("福岡")

        val result = useCase("福岡")

        assertIs<RunStatus.Error<String>>(result)
        assertEquals("すでに同じエリア名が登録されています", result.message)
    }

    @Test
    fun `invoke - 同名のエリアが既にある場合は登録されない`() = runTest {
        fakeAreasRepository.existingArea = createArea("福岡")

        useCase("福岡")

        assertEquals(null, fakeAreasRepository.addedArea)
    }

    // --- 正常系 ---

    @Test
    fun `invoke - 未登録の場合はSuccessを返す`() = runTest {
        val result = useCase("福岡")

        assertIs<RunStatus.Success<String>>(result)
    }

    private fun createArea(name: String) = Area(
        areaId = 1,
        name = name,
        count = 0,
        updatedDate = LocalDate(2026, 1, 1),
        sort = 1
    )

    private class FakeAreasRepositoryForAdd : AreasRepositoryContract {
        var existingArea: Area? = null
        var loadedName: String? = null
        var addedArea: Area? = null

        override suspend fun load(): List<Area> = emptyList()

        override suspend fun load(areaName: String): Area? {
            loadedName = areaName
            return existingArea
        }

        override suspend fun add(area: Area) {
            addedArea = area
        }

        override suspend fun edit(oldName: String, newName: String): RunStatus<String> =
            RunStatus.Success("")

        override suspend fun edit(area: Area): RunStatus<String> = RunStatus.Success("")

        override suspend fun editAll(areas: List<Area>): RunStatus<String> = RunStatus.Success("")

        override suspend fun loadByAreaId(areaId: Int): Area? = null

        override suspend fun delete(areaName: String): RunStatus<String> = RunStatus.Success("")
    }
}
