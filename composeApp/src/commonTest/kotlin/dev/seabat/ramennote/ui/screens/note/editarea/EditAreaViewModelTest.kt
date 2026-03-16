package dev.seabat.ramennote.ui.screens.note.editarea

import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.usecase.DeleteAreaUseCaseContract
import dev.seabat.ramennote.domain.usecase.LoadImageUseCaseContract
import dev.seabat.ramennote.domain.usecase.UpdateAreaImageUseCaseContract
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
    private val fakeUpdateAreaImageUseCase = FakeUpdateAreaImageUseCase()
    private val fakeLoadImageUseCase = FakeLoadImageUseCase()

    private lateinit var viewModel: EditAreaViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel =
            EditAreaViewModel(
                deleteAreaUseCase = fakeDeleteAreaUseCase,
                updateAreaUseCase = fakeUpdateAreaUseCase,
                updateAreaImageUseCase = fakeUpdateAreaImageUseCase,
                loadImageUseCase = fakeLoadImageUseCase
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

    // --- editArea ---

    @Test
    fun `editArea - 成功時にeditStateがSuccessになる`() = runTest {
        viewModel.currentAreaName = "旧名称"
        fakeUpdateAreaUseCase.result = RunStatus.Success("新名称")

        viewModel.editArea("新名称")

        assertIs<RunStatus.Success<String>>(viewModel.editState.value)
    }

    @Test
    fun `editArea - currentAreaNameとnewAreaNameがUseCaseに渡される`() = runTest {
        viewModel.currentAreaName = "旧名称"
        fakeUpdateAreaUseCase.result = RunStatus.Success("")

        viewModel.editArea("新名称")

        assertEquals("旧名称", fakeUpdateAreaUseCase.invokedOldName)
        assertEquals("新名称", fakeUpdateAreaUseCase.invokedNewName)
    }

    @Test
    fun `editArea - 成功後にcurrentAreaNameが更新される`() = runTest {
        viewModel.currentAreaName = "旧名称"
        fakeUpdateAreaUseCase.result = RunStatus.Success("")

        viewModel.editArea("新名称")

        assertEquals("新名称", viewModel.currentAreaName)
    }

    @Test
    fun `editArea - Errorの場合にeditStateがErrorになる`() = runTest {
        viewModel.currentAreaName = "存在しない"
        fakeUpdateAreaUseCase.result = RunStatus.Error("更新失敗")

        viewModel.editArea("新名称")

        assertIs<RunStatus.Error<String>>(viewModel.editState.value)
    }

    // --- deleteArea ---

    @Test
    fun `deleteArea - 成功時にeditStateがSuccessになる`() = runTest {
        fakeDeleteAreaUseCase.result = RunStatus.Success("")

        viewModel.deleteArea("東京")

        assertIs<RunStatus.Success<String>>(viewModel.editState.value)
    }

    @Test
    fun `deleteArea - areaNameがUseCaseに渡される`() = runTest {
        fakeDeleteAreaUseCase.result = RunStatus.Success("")

        viewModel.deleteArea("大阪")

        assertEquals("大阪", fakeDeleteAreaUseCase.invokedName)
    }

    @Test
    fun `deleteArea - Errorの場合にeditStateがErrorになる`() = runTest {
        fakeDeleteAreaUseCase.result = RunStatus.Error("削除失敗")

        viewModel.deleteArea("存在しない")

        assertIs<RunStatus.Error<String>>(viewModel.editState.value)
    }

    // --- fetchImage ---

    @Test
    fun `fetchImage - 成功時にimageStateがSuccessになる`() = runTest {
        val imageBytes = byteArrayOf(1, 2, 3)
        fakeUpdateAreaImageUseCase.result = RunStatus.Success(imageBytes)

        viewModel.fetchImage("東京")

        val state = viewModel.imageState.value
        assertIs<RunStatus.Success<ByteArray>>(state)
        assertEquals(imageBytes, state.data)
    }

    @Test
    fun `fetchImage - areaNameがUseCaseに渡される`() = runTest {
        fakeUpdateAreaImageUseCase.result = RunStatus.Success(byteArrayOf())

        viewModel.fetchImage("福岡")

        assertEquals("福岡", fakeUpdateAreaImageUseCase.invokedArea)
    }

    @Test
    fun `fetchImage - Errorの場合にimageStateがErrorになる`() = runTest {
        fakeUpdateAreaImageUseCase.result = RunStatus.Error("画像取得失敗")

        viewModel.fetchImage("東京")

        assertIs<RunStatus.Error<ByteArray>>(viewModel.imageState.value)
    }

    // --- loadImage ---

    @Test
    fun `loadImage - 成功時にimageStateがSuccessになる`() = runTest {
        val imageBytes = byteArrayOf(4, 5, 6)
        fakeLoadImageUseCase.result = RunStatus.Success(imageBytes)

        viewModel.loadImage("東京")

        val state = viewModel.imageState.value
        assertIs<RunStatus.Success<ByteArray>>(state)
        assertEquals(imageBytes, state.data)
    }

    @Test
    fun `loadImage - nameがUseCaseに渡される`() = runTest {
        fakeLoadImageUseCase.result = RunStatus.Success(byteArrayOf())

        viewModel.loadImage("大阪")

        assertEquals("大阪", fakeLoadImageUseCase.invokedName)
    }

    @Test
    fun `loadImage - Errorの場合にimageStateがErrorになる`() = runTest {
        fakeLoadImageUseCase.result = RunStatus.Error("読み込み失敗")

        viewModel.loadImage("東京")

        assertIs<RunStatus.Error<ByteArray>>(viewModel.imageState.value)
    }

    // --- resetImageState ---

    @Test
    fun `resetImageState - imageStateがIdleに戻る`() = runTest {
        fakeLoadImageUseCase.result = RunStatus.Success(byteArrayOf())
        viewModel.loadImage("東京")
        assertIs<RunStatus.Success<ByteArray>>(viewModel.imageState.value)

        viewModel.resetImageState()

        assertIs<RunStatus.Idle<ByteArray>>(viewModel.imageState.value)
    }

    // --- フェイク UseCase 実装 ---

    private class FakeDeleteAreaUseCase : DeleteAreaUseCaseContract {
        var invokedName: String? = null
        var result: RunStatus<String> = RunStatus.Success("")

        override suspend fun invoke(name: String): RunStatus<String> {
            invokedName = name
            return result
        }
    }

    private class FakeUpdateAreaUseCase : UpdateAreaUseCaseContract {
        var invokedOldName: String? = null
        var invokedNewName: String? = null
        var result: RunStatus<String> = RunStatus.Success("")

        override suspend fun invoke(oldName: String, newName: String): RunStatus<String> {
            invokedOldName = oldName
            invokedNewName = newName
            return result
        }
    }

    private class FakeUpdateAreaImageUseCase : UpdateAreaImageUseCaseContract {
        var invokedArea: String? = null
        var result: RunStatus<ByteArray> = RunStatus.Success(byteArrayOf())

        override suspend fun invoke(area: String): RunStatus<ByteArray> {
            invokedArea = area
            return result
        }
    }

    private class FakeLoadImageUseCase : LoadImageUseCaseContract {
        var invokedName: String? = null
        var result: RunStatus<ByteArray> = RunStatus.Success(byteArrayOf())

        override suspend fun invoke(name: String): RunStatus<ByteArray> {
            invokedName = name
            return result
        }
    }
}
