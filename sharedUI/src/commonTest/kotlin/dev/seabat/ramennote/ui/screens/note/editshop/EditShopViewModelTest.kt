package dev.seabat.ramennote.ui.screens.note.editshop

import dev.seabat.ramennote.domain.model.Area
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.model.Shop
import dev.seabat.ramennote.domain.usecase.DeleteShopAndImageUseCaseContract
import dev.seabat.ramennote.domain.usecase.LoadAreasUseCaseContract
import dev.seabat.ramennote.domain.usecase.LoadImageUseCaseContract
import dev.seabat.ramennote.domain.usecase.UpdateShopUseCaseContract
import dev.seabat.ramennote.ui.gallery.SharedImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class EditShopViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val fakeUpdateShopUseCase = FakeUpdateShopUseCase()
    private val fakeLoadImageUseCase = FakeLoadImageUseCase()
    private val fakeDeleteShopAndImageUseCase = FakeDeleteShopAndImageUseCase()
    private val fakeLoadAreasUseCase = FakeLoadAreasUseCase()

    private lateinit var viewModel: EditShopViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel =
            EditShopViewModel(
                updateShopUseCase = fakeUpdateShopUseCase,
                loadImageUseCase = fakeLoadImageUseCase,
                deleteShopAndImageUseCase = fakeDeleteShopAndImageUseCase,
                loadAreasUseCase = fakeLoadAreasUseCase
            )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // --- 初期状態 ---

    @Test
    fun `saveState - 初期状態はIdle`() {
        assertIs<RunStatus.Idle<String>>(viewModel.saveState.value)
    }

    @Test
    fun `deleteState - 初期状態はIdle`() {
        assertIs<RunStatus.Idle<String>>(viewModel.deleteState.value)
    }

    @Test
    fun `shopImage - 初期状態はnull`() {
        assertNull(viewModel.shopImage.value)
    }

    @Test
    fun `areasState - 初期状態は空リスト`() {
        assertEquals(emptyList(), viewModel.areasState.value)
    }

    // --- loadImage ---

    @Test
    fun `loadImage - photoName1が空の場合shopImageはnullになる`() = runTest {
        val shop = createTestShop(photoName1 = "")

        viewModel.loadImage(shop)

        assertNull(viewModel.shopImage.value)
    }

    // NOTE: SharedImage(ByteArray) は BitmapFactory を使用するため JVM 単体テストでは実行不可。
    // 画像ロード成功時の shopImage non-null は instrumented test で検証する。

    @Test
    fun `loadImage - 画像読み込みがErrorの場合shopImageはnullになる`() = runTest {
        fakeLoadImageUseCase.result = RunStatus.Error("読み込み失敗")
        val shop = createTestShop(photoName1 = "photo.jpg")

        viewModel.loadImage(shop)

        assertNull(viewModel.shopImage.value)
    }

    @Test
    fun `loadImage - photoName1がUseCaseに渡される`() = runTest {
        // SharedImage(ByteArray) は BitmapFactory を使用するため Error を返すフェイクで UseCase 呼び出しだけ検証する
        fakeLoadImageUseCase.result = RunStatus.Error("テスト用エラー")
        val shop = createTestShop(photoName1 = "ramen.jpg")

        viewModel.loadImage(shop)

        assertEquals("ramen.jpg", fakeLoadImageUseCase.invokedName)
    }

    // --- setImage ---

    @Test
    fun `setImage - nullを渡すとshopImageがnullになる`() = runTest {
        viewModel.setImage(null)

        assertNull(viewModel.shopImage.value)
    }

    @Test
    fun `setImage - SharedImageを渡すとshopImageにセットされる`() = runTest {
        val image = SharedImage()

        viewModel.setImage(image)

        assertEquals(image, viewModel.shopImage.value)
    }

    // --- updateShop ---

    @Test
    fun `updateShop - 成功時にsaveStateがSuccessになる`() = runTest {
        val shop = createTestShop()

        viewModel.updateShop(shop, null, 1)

        assertIs<RunStatus.Success<String>>(viewModel.saveState.value)
    }

    @Test
    fun `updateShop - shop・byteArray・oldAreaIdがUseCaseに渡される`() = runTest {
        val shop = createTestShop(id = 5)

        viewModel.updateShop(shop, null, 3)

        assertEquals(shop, fakeUpdateShopUseCase.invokedShop)
        assertNull(fakeUpdateShopUseCase.invokedByteArray) // sharedImage = null
        assertEquals(3, fakeUpdateShopUseCase.invokedOldAreaId)
    }

    @Test
    fun `updateShop - UseCaseが例外をスローした場合saveStateがErrorになる`() = runTest {
        fakeUpdateShopUseCase.shouldThrow = true
        val shop = createTestShop()

        viewModel.updateShop(shop, null, 1)

        val state = viewModel.saveState.value
        assertIs<RunStatus.Error<String>>(state)
        assertNotNull(state.message)
    }

    // --- deleteShop ---

    @Test
    fun `deleteShop - 成功時にdeleteStateがSuccessになる`() = runTest {
        fakeDeleteShopAndImageUseCase.result = RunStatus.Success("")

        viewModel.deleteShop(1)

        assertIs<RunStatus.Success<String>>(viewModel.deleteState.value)
    }

    @Test
    fun `deleteShop - shopIdがUseCaseに渡される`() = runTest {
        fakeDeleteShopAndImageUseCase.result = RunStatus.Success("")

        viewModel.deleteShop(42)

        assertEquals(42, fakeDeleteShopAndImageUseCase.invokedShopId)
    }

    @Test
    fun `deleteShop - Errorの場合deleteStateがErrorになる`() = runTest {
        fakeDeleteShopAndImageUseCase.result = RunStatus.Error("削除失敗")

        viewModel.deleteShop(1)

        assertIs<RunStatus.Error<String>>(viewModel.deleteState.value)
    }

    // --- setSaveStateToIdle ---

    @Test
    fun `setSaveStateToIdle - saveStateがIdleに戻る`() = runTest {
        viewModel.updateShop(createTestShop(), null, 1)
        assertIs<RunStatus.Success<String>>(viewModel.saveState.value)

        viewModel.setSaveStateToIdle()

        assertIs<RunStatus.Idle<String>>(viewModel.saveState.value)
    }

    // --- setDeleteStateToIdle ---

    @Test
    fun `setDeleteStateToIdle - deleteStateがIdleに戻る`() = runTest {
        fakeDeleteShopAndImageUseCase.result = RunStatus.Success("")
        viewModel.deleteShop(1)
        assertIs<RunStatus.Success<String>>(viewModel.deleteState.value)

        viewModel.setDeleteStateToIdle()

        assertIs<RunStatus.Idle<String>>(viewModel.deleteState.value)
    }

    // --- loadAreas ---

    @Test
    fun `loadAreas - UseCaseが返したエリアリストがareasStateにセットされる`() = runTest {
        val areas =
            listOf(
                Area(areaId = 1, name = "東京", count = 0, updatedDate = LocalDate(2024, 1, 1), sort = 1),
                Area(areaId = 2, name = "大阪", count = 0, updatedDate = LocalDate(2024, 1, 1), sort = 2)
            )
        fakeLoadAreasUseCase.result = areas

        viewModel.loadAreas()

        assertEquals(areas, viewModel.areasState.value)
    }

    @Test
    fun `loadAreas - UseCaseが空リストを返した場合areasStateは空リストになる`() = runTest {
        fakeLoadAreasUseCase.result = emptyList()

        viewModel.loadAreas()

        assertEquals(emptyList(), viewModel.areasState.value)
    }

    // --- ヘルパー ---

    private fun createTestShop(
        id: Int = 1,
        name: String = "テスト店",
        areaId: Int = 1,
        photoName1: String = ""
    ) = Shop(
        id = id,
        name = name,
        areaId = areaId,
        photoName1 = photoName1
    )

    // --- フェイク UseCase 実装 ---

    private class FakeUpdateShopUseCase : UpdateShopUseCaseContract {
        var invokedShop: Shop? = null
        var invokedByteArray: ByteArray? = null
        var invokedOldAreaId: Int? = null
        var shouldThrow = false

        override suspend fun invoke(shop: Shop, byteArray: ByteArray?, oldAreaId: Int) {
            if (shouldThrow) throw RuntimeException("更新エラー")
            invokedShop = shop
            invokedByteArray = byteArray
            invokedOldAreaId = oldAreaId
        }
    }

    private class FakeLoadImageUseCase : LoadImageUseCaseContract {
        var invokedName: String? = null
        var result: RunStatus<ByteArray> = RunStatus.Idle()

        override suspend fun invoke(name: String): RunStatus<ByteArray> {
            invokedName = name
            return result
        }
    }

    private class FakeDeleteShopAndImageUseCase : DeleteShopAndImageUseCaseContract {
        var invokedShopId: Int? = null
        var result: RunStatus<String> = RunStatus.Success("")

        override suspend fun invoke(shopId: Int): RunStatus<String> {
            invokedShopId = shopId
            return result
        }
    }

    private class FakeLoadAreasUseCase : LoadAreasUseCaseContract {
        var result: List<Area> = emptyList()
        override suspend fun invoke(): List<Area> = result
    }
}
