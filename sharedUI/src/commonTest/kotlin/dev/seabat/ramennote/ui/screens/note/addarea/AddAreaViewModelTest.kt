package dev.seabat.ramennote.ui.screens.note.addarea

import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.usecase.AddAreaUseCaseContract
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

@OptIn(ExperimentalCoroutinesApi::class)
class AddAreaViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val fakeAddAreaUseCase = FakeAddAreaUseCase()
    private val fakeFetchUnsplashImageUseCase = FakeFetchAndSaveUnsplashImageUseCase()

    private lateinit var viewModel: AddAreaViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AddAreaViewModel(
            addAreaUseCase = fakeAddAreaUseCase,
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
        fakeAddAreaUseCase.result = RunStatus.Success("東京")
        fakeFetchUnsplashImageUseCase.result = RunStatus.Success(imageBytes)

        viewModel.addArea("東京")

        val state = viewModel.addState.value
        assertIs<RunStatus.Success<ByteArray>>(state)
        assertEquals(imageBytes, state.data)
    }

    @Test
    fun `addArea - addAreaUseCaseが正しいエリア名で呼ばれる`() = runTest {
        fakeAddAreaUseCase.result = RunStatus.Success("大阪")
        fakeFetchUnsplashImageUseCase.result = RunStatus.Success(byteArrayOf())

        viewModel.addArea("大阪")

        assertEquals("大阪", fakeAddAreaUseCase.invokedAreaName)
    }

    @Test
    fun `addArea - エリア名の前後スペースがトリムされてaddが呼ばれる`() = runTest {
        fakeAddAreaUseCase.result = RunStatus.Success("福岡")
        fakeFetchUnsplashImageUseCase.result = RunStatus.Success(byteArrayOf())

        viewModel.addArea("  福岡  ")

        assertEquals("福岡", fakeAddAreaUseCase.invokedAreaName)
    }

    @Test
    fun `addArea - fetchUnsplashImageUseCaseがErrorを返した場合、addStateがErrorになる`() = runTest {
        fakeAddAreaUseCase.result = RunStatus.Success("京都")
        fakeFetchUnsplashImageUseCase.result = RunStatus.Error("画像取得失敗")

        viewModel.addArea("京都")

        val state = viewModel.addState.value
        assertIs<RunStatus.Error<ByteArray>>(state)
        assertEquals("画像取得失敗", state.message)
    }

    @Test
    fun `addArea - fetchUnsplashImageUseCaseに正しいクエリが渡される`() = runTest {
        fakeAddAreaUseCase.result = RunStatus.Success("北海道")
        fakeFetchUnsplashImageUseCase.result = RunStatus.Success(byteArrayOf())

        viewModel.addArea("北海道")

        assertEquals("北海道", fakeFetchUnsplashImageUseCase.invokedQuery)
    }

    // --- フェイク実装 ---

    private class FakeAddAreaUseCase : AddAreaUseCaseContract {
        var invokedAreaName: String? = null
        var result: RunStatus<String> = RunStatus.Success("")

        override suspend fun invoke(areaName: String): RunStatus<String> {
            invokedAreaName = areaName
            return result
        }
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
