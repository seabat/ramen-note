package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.AreasRepositoryContract
import dev.seabat.ramennote.data.repository.LocalImageRepositoryContract
import dev.seabat.ramennote.domain.model.Area
import dev.seabat.ramennote.domain.model.RunStatus
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertIs

class UpdateAreaImageUseCaseTest {

    private val fakeAreasRepository = FakeAreasRepository()
    private val fakeLocalImageRepository = FakeLocalImageRepository()
    private val fakeFetchAndSaveUnsplashImageUseCase = FakeFetchAndSaveUnsplashImageUseCase()

    private val useCase = UpdateAreaImageUseCase(
        areasRepository = fakeAreasRepository,
        localAreaImageRepository = fakeLocalImageRepository,
        fetchAndSaveUnsplashImageUseCase = fakeFetchAndSaveUnsplashImageUseCase
    )

    // --- エリアが存在しない場合 ---

    @Test
    fun `invoke - エリアが存在しない場合はRunStatus_Errorを返す`() = runTest {
        fakeAreasRepository.area = null
        fakeLocalImageRepository.loadResult = RunStatus.Success(byteArrayOf(1))

        val result = useCase("存在しないエリア")

        assertIs<RunStatus.Error<ByteArray>>(result)
        assertEquals("エリアが登録されていません", result.message)
    }

    @Test
    fun `invoke - エリアが存在しない場合はUnsplash取得が呼ばれない`() = runTest {
        fakeAreasRepository.area = null
        fakeLocalImageRepository.loadResult = RunStatus.Success(byteArrayOf(1))

        useCase("存在しないエリア")

        assertEquals(null, fakeFetchAndSaveUnsplashImageUseCase.invokedQuery)
    }

    // --- ローカル画像が存在しない場合 ---

    @Test
    fun `invoke - ローカル画像がない場合はUnsplash取得が呼ばれる`() = runTest {
        fakeAreasRepository.area = createTestArea(name = "渋谷", updatedDate = LocalDate(2000, 1, 1))
        fakeLocalImageRepository.loadResult = RunStatus.Error("画像なし")
        fakeFetchAndSaveUnsplashImageUseCase.result = RunStatus.Success(byteArrayOf(1, 2, 3))

        useCase("渋谷")

        assertEquals("渋谷", fakeFetchAndSaveUnsplashImageUseCase.invokedQuery)
    }

    @Test
    fun `invoke - ローカル画像がない場合はUnsplash取得の結果をそのまま返す`() = runTest {
        val imageBytes = byteArrayOf(1, 2, 3)
        fakeAreasRepository.area = createTestArea(name = "渋谷", updatedDate = LocalDate(2000, 1, 1))
        fakeLocalImageRepository.loadResult = RunStatus.Error("画像なし")
        fakeFetchAndSaveUnsplashImageUseCase.result = RunStatus.Success(imageBytes)

        val result = useCase("渋谷")

        assertIs<RunStatus.Success<ByteArray>>(result)
        assertEquals(imageBytes, result.data)
    }

    // --- ローカル画像が存在する + updatedDate が過去の場合 ---

    @Test
    fun `invoke - ローカル画像があり更新日が過去の場合はUnsplash取得が呼ばれる`() = runTest {
        fakeAreasRepository.area = createTestArea(name = "新宿", updatedDate = LocalDate(2000, 1, 1))
        fakeLocalImageRepository.loadResult = RunStatus.Success(byteArrayOf(10, 20))
        fakeFetchAndSaveUnsplashImageUseCase.result = RunStatus.Success(byteArrayOf(30, 40))

        useCase("新宿")

        assertEquals("新宿", fakeFetchAndSaveUnsplashImageUseCase.invokedQuery)
    }

    @Test
    fun `invoke - ローカル画像があり更新日が過去の場合はUnsplash取得の結果をそのまま返す`() = runTest {
        val newImageBytes = byteArrayOf(30, 40)
        fakeAreasRepository.area = createTestArea(name = "新宿", updatedDate = LocalDate(2000, 1, 1))
        fakeLocalImageRepository.loadResult = RunStatus.Success(byteArrayOf(10, 20))
        fakeFetchAndSaveUnsplashImageUseCase.result = RunStatus.Success(newImageBytes)

        val result = useCase("新宿")

        assertIs<RunStatus.Success<ByteArray>>(result)
        assertEquals(newImageBytes, result.data)
    }

    // --- ローカル画像が存在する + updatedDate が今日または未来の場合 ---

    @Test
    fun `invoke - ローカル画像があり更新日が未来の場合はUnsplash取得が呼ばれない`() = runTest {
        fakeAreasRepository.area = createTestArea(name = "池袋", updatedDate = LocalDate(9999, 12, 31))
        fakeLocalImageRepository.loadResult = RunStatus.Success(byteArrayOf(10, 20))

        useCase("池袋")

        assertEquals(null, fakeFetchAndSaveUnsplashImageUseCase.invokedQuery)
    }

    @Test
    fun `invoke - ローカル画像があり更新日が未来の場合はRunStatus_Errorを返す`() = runTest {
        fakeAreasRepository.area = createTestArea(name = "池袋", updatedDate = LocalDate(9999, 12, 31))
        fakeLocalImageRepository.loadResult = RunStatus.Success(byteArrayOf(10, 20))

        val result = useCase("池袋")

        assertIs<RunStatus.Error<ByteArray>>(result)
        assertContains(result.message ?: "", "本日は画像を変更できません")
    }

    // --- エリア名の受け渡し ---

    @Test
    fun `invoke - エリア名がAreasRepositoryに渡される`() = runTest {
        fakeAreasRepository.area = createTestArea(name = "上野", updatedDate = LocalDate(2000, 1, 1))
        fakeLocalImageRepository.loadResult = RunStatus.Success(byteArrayOf(10, 20))
        fakeFetchAndSaveUnsplashImageUseCase.result = RunStatus.Success(byteArrayOf())

        useCase("上野")

        assertEquals("上野", fakeAreasRepository.loadedAreaName)
    }

    // --- ヘルパー ---

    private fun createTestArea(name: String, updatedDate: LocalDate) = Area(
        areaId = 1,
        name = name,
        updatedDate = updatedDate,
        count = 0,
        sort = 1
    )

    // --- フェイク実装 ---

    private class FakeAreasRepository : AreasRepositoryContract {
        var area: Area? = null
        var loadedAreaName: String? = null

        override suspend fun loadByAreaId(areaId: Int): Area? = area

        override suspend fun load(): List<Area> = emptyList()
        override suspend fun load(areaName: String): Area? {
            loadedAreaName = areaName
            return area
        }
        override suspend fun add(area: Area) = Unit
        override suspend fun edit(oldName: String, newName: String): RunStatus<String> = RunStatus.Success("")
        override suspend fun edit(area: Area): RunStatus<String> = RunStatus.Success("")
        override suspend fun editAll(areas: List<Area>): RunStatus<String> = RunStatus.Success("")
        override suspend fun delete(areaName: String): RunStatus<String> = RunStatus.Success("")
    }

    private class FakeLocalImageRepository : LocalImageRepositoryContract {
        var loadResult: RunStatus<ByteArray> = RunStatus.Error("未設定")

        override suspend fun load(name: String): RunStatus<ByteArray> = loadResult

        override suspend fun save(imageBytes: ByteArray, name: String) = Unit
        override suspend fun getFilePath(name: String): String? = null
        override suspend fun rename(oldName: String, newName: String) = Unit
        override suspend fun delete(name: String) = Unit
    }

    private class FakeFetchAndSaveUnsplashImageUseCase : FetchAndSaveUnsplashImageUseCaseContract {
        var invokedQuery: String? = null
        var result: RunStatus<ByteArray> = RunStatus.Success(byteArrayOf())

        override suspend fun invoke(query: String): RunStatus<ByteArray> {
            invokedQuery = query
            return result
        }
    }
}
