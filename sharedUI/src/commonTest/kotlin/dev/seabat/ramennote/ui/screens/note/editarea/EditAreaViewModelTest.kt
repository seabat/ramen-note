package dev.seabat.ramennote.ui.screens.note.editarea

import dev.seabat.ramennote.domain.model.Area
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.usecase.DeleteAreaUseCaseContract
import dev.seabat.ramennote.domain.usecase.LoadAreaImageUseCaseContract
import dev.seabat.ramennote.domain.usecase.LoadAreasUseCaseContract
import dev.seabat.ramennote.domain.usecase.FetchUnsplashImageUseCaseContract
import dev.seabat.ramennote.domain.usecase.UpdateAreaUseCaseContract
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
class EditAreaViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val fakeDeleteAreaUseCase = FakeDeleteAreaUseCase()
    private val fakeUpdateAreaUseCase = FakeUpdateAreaUseCase()
    private val fakeFetchUnsplashImageUseCase = FakeFetchUnsplashImageUseCase()
    private val fakeLoadAreaImageUseCase = FakeLoadAreaImageUseCase()
    private val fakeLoadAreasUseCase = FakeLoadAreasUseCase()

    private lateinit var viewModel: EditAreaViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel =
            EditAreaViewModel(
                deleteAreaUseCase = fakeDeleteAreaUseCase,
                updateAreaUseCase = fakeUpdateAreaUseCase,
                fetchUnsplashImageUseCase = fakeFetchUnsplashImageUseCase,
                loadAreaImageUseCase = fakeLoadAreaImageUseCase,
                loadAreasUseCase = fakeLoadAreasUseCase
            )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // --- 初期状態 ---

    @Test
    fun `deleteState - 初期状態はIdle`() {
        assertIs<RunStatus.Idle<String>>(viewModel.deleteState.value)
    }

    @Test
    fun `editState - 初期状態はIdle`() {
        assertIs<RunStatus.Idle<String>>(viewModel.editState.value)
    }

    @Test
    fun `imageState - 初期状態はIdle`() {
        assertIs<RunStatus.Idle<ByteArray>>(viewModel.imageState.value)
    }

    @Test
    fun `areaName - 初期状態は空文字`() {
        assertEquals("", viewModel.areaName.value)
    }

    // --- editArea ---

    @Test
    fun `editArea - 成功時にeditStateがSuccessになる`() = runTest {
        fakeUpdateAreaUseCase.result = RunStatus.Success("")

        viewModel.editArea(1, "新名称")

        assertIs<RunStatus.Success<String>>(viewModel.editState.value)
    }

    @Test
    fun `editArea - areaIdとnewAreaNameがUseCaseに渡される`() = runTest {
        fakeUpdateAreaUseCase.result = RunStatus.Success("")

        viewModel.editArea(1, "新名称")

        assertEquals(1, fakeUpdateAreaUseCase.invokedAreaId)
        assertEquals("新名称", fakeUpdateAreaUseCase.invokedNewName)
    }

    @Test
    fun `editArea - 成功後にareaNameが更新される`() = runTest {
        fakeUpdateAreaUseCase.result = RunStatus.Success("")

        viewModel.editArea(1, "新名称")

        assertEquals("新名称", viewModel.areaName.value)
    }

    @Test
    fun `editArea - Errorの場合にeditStateがErrorになる`() = runTest {
        fakeUpdateAreaUseCase.result = RunStatus.Error("更新失敗")

        viewModel.editArea(1, "新名称")

        assertIs<RunStatus.Error<String>>(viewModel.editState.value)
    }

    // --- deleteArea ---

    @Test
    fun `deleteArea - 成功時にdeleteStateがSuccessになる`() = runTest {
        fakeDeleteAreaUseCase.result = RunStatus.Success("")

        viewModel.deleteArea(1)

        assertIs<RunStatus.Success<String>>(viewModel.deleteState.value)
    }

    @Test
    fun `deleteArea - areaIdがUseCaseに渡される`() = runTest {
        fakeDeleteAreaUseCase.result = RunStatus.Success("")

        viewModel.deleteArea(2)

        assertEquals(2, fakeDeleteAreaUseCase.invokedAreaId)
    }

    @Test
    fun `deleteArea - Errorの場合にdeleteStateがErrorになる`() = runTest {
        fakeDeleteAreaUseCase.result = RunStatus.Error("削除失敗")

        viewModel.deleteArea(1)

        assertIs<RunStatus.Error<String>>(viewModel.deleteState.value)
    }

    // --- fetchNewImage ---

    @Test
    fun `fetchNewImage - 成功時にimageStateがSuccessになる`() = runTest {
        val imageBytes = byteArrayOf(1, 2, 3)
        fakeFetchUnsplashImageUseCase.result = RunStatus.Success(imageBytes)

        viewModel.fetchNewImage("テストエリア")

        val state = viewModel.imageState.value
        assertIs<RunStatus.Success<ByteArray>>(state)
        assertEquals(imageBytes, state.data)
    }

    @Test
    fun `fetchNewImage - エリア名がUseCaseに渡される`() = runTest {
        fakeFetchUnsplashImageUseCase.result = RunStatus.Success(byteArrayOf())

        viewModel.fetchNewImage("別エリア")

        assertEquals("別エリア", fakeFetchUnsplashImageUseCase.invokedAreaName)
    }

    @Test
    fun `fetchNewImage - Errorの場合にimageStateがErrorになる`() = runTest {
        fakeFetchUnsplashImageUseCase.result = RunStatus.Error("画像取得失敗")

        viewModel.fetchNewImage("テストエリア")

        assertIs<RunStatus.Error<ByteArray>>(viewModel.imageState.value)
    }

    // --- loadAreaName ---

    @Test
    fun `loadAreaName - areaIdに対応するエリア名がareaNameにセットされる`() = runTest {
        fakeLoadAreasUseCase.areas = listOf(createTestArea(areaId = 1, name = "東京"))

        viewModel.loadAreaName(1)

        assertEquals("東京", viewModel.areaName.value)
    }

    // --- loadImage ---

    @Test
    fun `loadImage - 成功時にimageStateがSuccessになる`() = runTest {
        val imageBytes = byteArrayOf(4, 5, 6)
        fakeLoadAreaImageUseCase.result = RunStatus.Success(imageBytes)

        viewModel.loadImage(1)

        val state = viewModel.imageState.value
        assertIs<RunStatus.Success<ByteArray>>(state)
        assertEquals(imageBytes, state.data)
    }

    @Test
    fun `loadImage - areaIdがUseCaseに渡される`() = runTest {
        fakeLoadAreaImageUseCase.result = RunStatus.Success(byteArrayOf())

        viewModel.loadImage(2)

        assertEquals(2, fakeLoadAreaImageUseCase.invokedAreaId)
    }

    @Test
    fun `loadImage - Errorの場合にimageStateがErrorになる`() = runTest {
        fakeLoadAreaImageUseCase.result = RunStatus.Error("読み込み失敗")

        viewModel.loadImage(1)

        assertIs<RunStatus.Error<ByteArray>>(viewModel.imageState.value)
    }

    // --- resetImageState ---

    @Test
    fun `resetImageState - 表示中の画像がある場合はSuccessに復元される`() = runTest {
        val displayed = byteArrayOf(1, 2, 3)
        fakeLoadAreaImageUseCase.result = RunStatus.Success(displayed)
        viewModel.loadImage(1)
        assertIs<RunStatus.Success<ByteArray>>(viewModel.imageState.value)

        viewModel.resetImageState()

        val state = viewModel.imageState.value
        assertIs<RunStatus.Success<ByteArray>>(state)
        assertEquals(displayed.toList(), state.data?.toList())
    }

    @Test
    fun `resetImageState - 表示中の画像がない場合はIdleに戻る`() = runTest {
        fakeFetchUnsplashImageUseCase.result = RunStatus.Error("画像取得失敗")
        viewModel.fetchNewImage("テストエリア")
        assertIs<RunStatus.Error<ByteArray>>(viewModel.imageState.value)

        viewModel.resetImageState()

        assertIs<RunStatus.Idle<ByteArray>>(viewModel.imageState.value)
    }

    // --- ヘルパー ---

    private fun createTestArea(areaId: Int, name: String) = Area(
        areaId = areaId,
        name = name,
        count = 0,
        updatedDate = kotlinx.datetime.LocalDate(2026, 1, 1),
        sort = 1
    )

    // --- フェイク UseCase 実装 ---

    private class FakeDeleteAreaUseCase : DeleteAreaUseCaseContract {
        var invokedAreaId: Int? = null
        var result: RunStatus<String> = RunStatus.Success("")

        override suspend fun invoke(areaId: Int): RunStatus<String> {
            invokedAreaId = areaId
            return result
        }
    }

    private class FakeUpdateAreaUseCase : UpdateAreaUseCaseContract {
        var invokedAreaId: Int? = null
        var invokedNewName: String? = null
        var invokedByteArray: ByteArray? = null
        var result: RunStatus<String> = RunStatus.Success("")

        override suspend fun invoke(areaId: Int, newName: String, byteArray: ByteArray?): RunStatus<String> {
            invokedAreaId = areaId
            invokedNewName = newName
            invokedByteArray = byteArray
            return result
        }
    }

    private class FakeFetchUnsplashImageUseCase : FetchUnsplashImageUseCaseContract {
        var invokedAreaName: String? = null
        var result: RunStatus<ByteArray> = RunStatus.Success(byteArrayOf())

        override suspend fun invoke(areaName: String): RunStatus<ByteArray> {
            invokedAreaName = areaName
            return result
        }
    }

    private class FakeLoadAreaImageUseCase : LoadAreaImageUseCaseContract {
        var invokedAreaId: Int? = null
        var result: RunStatus<ByteArray> = RunStatus.Success(byteArrayOf())

        override suspend fun invoke(areaId: Int): RunStatus<ByteArray> {
            invokedAreaId = areaId
            return result
        }
    }

    private class FakeLoadAreasUseCase : LoadAreasUseCaseContract {
        var areas: List<Area> = emptyList()

        override suspend fun invoke(): List<Area> = areas
    }
}
