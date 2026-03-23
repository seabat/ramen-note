package dev.seabat.ramennote.ui.screens.note.addarea

import dev.seabat.ramennote.data.repository.AreasRepositoryContract
import dev.seabat.ramennote.domain.model.Area
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.usecase.FetchAndSaveUnsplashImageUseCaseContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class AddAreaViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val fakeAreasRepository = FakeAreasRepository()
    private val fakeFetchUnsplashImageUseCase = FakeFetchAndSaveUnsplashImageUseCase()

    private lateinit var viewModel: AddAreaViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AddAreaViewModel(
            areasRepository = fakeAreasRepository,
            fetchUnsplashImageUseCase = fakeFetchUnsplashImageUseCase
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // --- addState 初期状態 ---

    @Test
    fun `addState - 初期状態はIdle`() {
        assertIs<RunStatus.Idle<ByteArray>>(viewModel.addState.value)
    }

    // --- addArea ---

    @Test
    fun `addArea - 成功時にaddStateがSuccessになる`() = runTest {
        val imageBytes = byteArrayOf(1, 2, 3)
        fakeFetchUnsplashImageUseCase.result = RunStatus.Success(imageBytes)

        viewModel.addArea("東京")

        val state = viewModel.addState.value
        assertIs<RunStatus.Success<ByteArray>>(state)
        assertEquals(imageBytes, state.data)
    }

    @Test
    fun `addArea - areasRepositoryのaddが呼ばれる`() = runTest {
        fakeFetchUnsplashImageUseCase.result = RunStatus.Success(byteArrayOf())

        viewModel.addArea("大阪")

        assertEquals("大阪", fakeAreasRepository.addedArea?.name)
        assertEquals(0, fakeAreasRepository.addedArea?.count)
    }

    @Test
    fun `addArea - エリア名の前後スペースがトリムされてaddが呼ばれる`() = runTest {
        fakeFetchUnsplashImageUseCase.result = RunStatus.Success(byteArrayOf())

        viewModel.addArea("  福岡  ")

        assertEquals("福岡", fakeAreasRepository.addedArea?.name)
    }

    @Test
    fun `addArea - fetchUnsplashImageUseCaseがErrorを返した場合、addStateがErrorになる`() = runTest {
        fakeFetchUnsplashImageUseCase.result = RunStatus.Error("画像取得失敗")

        viewModel.addArea("京都")

        val state = viewModel.addState.value
        assertIs<RunStatus.Error<ByteArray>>(state)
        assertEquals("画像取得失敗", state.message)
    }

    @Test
    fun `addArea - fetchUnsplashImageUseCaseに正しいクエリが渡される`() = runTest {
        fakeFetchUnsplashImageUseCase.result = RunStatus.Success(byteArrayOf())

        viewModel.addArea("北海道")

        assertEquals("北海道", fakeFetchUnsplashImageUseCase.invokedQuery)
    }

    // --- フェイク実装 ---

    private class FakeAreasRepository : AreasRepositoryContract {
        var addedArea: Area? = null

        override suspend fun load(): List<Area> = emptyList()
        override suspend fun load(areaName: String): Area? = null
        override suspend fun loadByAreaId(areaId: Int): Area? = null

        override suspend fun add(area: Area) {
            addedArea = area
        }

        override suspend fun edit(oldName: String, newName: String): RunStatus<String> =
            RunStatus.Success("")

        override suspend fun edit(area: Area): RunStatus<String> =
            RunStatus.Success("")

        override suspend fun editAll(areas: List<Area>): RunStatus<String> =
            RunStatus.Success("")

        override suspend fun delete(areaName: String): RunStatus<String> =
            RunStatus.Success("")
    }

    private class FakeFetchAndSaveUnsplashImageUseCase : FetchAndSaveUnsplashImageUseCaseContract {
        var invokedQuery: String? = null
        var result: RunStatus<ByteArray> = RunStatus.Idle()

        override suspend fun invoke(query: String): RunStatus<ByteArray> {
            invokedQuery = query
            return result
        }
    }
}
