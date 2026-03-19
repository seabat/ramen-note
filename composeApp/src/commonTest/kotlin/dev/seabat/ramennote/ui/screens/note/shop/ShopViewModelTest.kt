package dev.seabat.ramennote.ui.screens.note.shop

import dev.seabat.ramennote.domain.model.Area
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.model.Shop
import dev.seabat.ramennote.domain.usecase.LoadAreasUseCaseContract
import dev.seabat.ramennote.domain.usecase.LoadImageUseCaseContract
import dev.seabat.ramennote.domain.usecase.LoadShopUseCaseContract
import dev.seabat.ramennote.domain.usecase.SwitchFavoriteUseCaseContract
import dev.seabat.ramennote.domain.usecase.UpdateScheduleInShopUseCaseContract
import dev.seabat.ramennote.domain.usecase.UpdateStarUseCaseContract
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
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class ShopViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val fakeLoadShopUseCase = FakeLoadShopUseCase()
    private val fakeLoadImageUseCase = FakeLoadImageUseCase()
    private val fakeUpdateScheduleUseCase = FakeUpdateScheduleInShopUseCase()
    private val fakeSwitchFavoriteUseCase = FakeSwitchFavoriteUseCase()
    private val fakeUpdateStarUseCase = FakeUpdateStarUseCase()
    private val fakeLoadAreasUseCase = FakeLoadAreasUseCase()

    private lateinit var viewModel: ShopViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel =
            ShopViewModel(
                loadShopUseCase = fakeLoadShopUseCase,
                loadImageUseCase = fakeLoadImageUseCase,
                addScheduleUseCase = fakeUpdateScheduleUseCase,
                switchFavoriteUseCase = fakeSwitchFavoriteUseCase,
                updateStarUseCase = fakeUpdateStarUseCase,
                loadAreasUseCase = fakeLoadAreasUseCase
            )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // --- loadShopAndImage ---

    @Test
    fun `loadShopAndImage - Shopと画像を正常に読み込む`() = runTest {
        val shop = createTestShop(photoName1 = "photo.jpg")
        val imageBytes = byteArrayOf(1, 2, 3)
        fakeLoadShopUseCase.result = shop
        fakeLoadImageUseCase.result = RunStatus.Success(imageBytes)

        viewModel.loadShopAndImage(1)

        assertEquals(shop, viewModel.shop.value)
        assertEquals(imageBytes, viewModel.shopImage.value)
    }

    @Test
    fun `loadShopAndImage - Shopがnullの場合、画像もnullになる`() = runTest {
        fakeLoadShopUseCase.result = null

        viewModel.loadShopAndImage(1)

        assertNull(viewModel.shop.value)
        assertNull(viewModel.shopImage.value)
    }

    @Test
    fun `loadShopAndImage - photoName1が空の場合、画像はnullになる`() = runTest {
        val shop = createTestShop(photoName1 = "")
        fakeLoadShopUseCase.result = shop

        viewModel.loadShopAndImage(1)

        assertEquals(shop, viewModel.shop.value)
        assertNull(viewModel.shopImage.value)
    }

    @Test
    fun `loadShopAndImage - 画像読み込みがErrorの場合、画像はnullになる`() = runTest {
        val shop = createTestShop(photoName1 = "photo.jpg")
        fakeLoadShopUseCase.result = shop
        fakeLoadImageUseCase.result = RunStatus.Error("読み込み失敗")

        viewModel.loadShopAndImage(1)

        assertEquals(shop, viewModel.shop.value)
        assertNull(viewModel.shopImage.value)
    }

    @Test
    fun `loadShopAndImage - 画像読み込みがLoadingの場合、画像は更新されない`() = runTest {
        val shop = createTestShop(photoName1 = "photo.jpg")
        fakeLoadShopUseCase.result = shop
        fakeLoadImageUseCase.result = RunStatus.Loading()

        viewModel.loadShopAndImage(1)

        assertEquals(shop, viewModel.shop.value)
        assertNull(viewModel.shopImage.value)
    }

    @Test
    fun `loadShopAndImage - ShopのareaIdに対応するエリア名がareaNameにセットされる`() = runTest {
        val shop = createTestShop(areaId = 2)
        fakeLoadShopUseCase.result = shop
        fakeLoadImageUseCase.result = RunStatus.Success(byteArrayOf())
        fakeLoadAreasUseCase.result =
            listOf(
                Area(areaId = 1, name = "東京", count = 0, updatedDate = LocalDate(2024, 1, 1), sort = 1),
                Area(areaId = 2, name = "大阪", count = 0, updatedDate = LocalDate(2024, 1, 1), sort = 2)
            )

        viewModel.loadShopAndImage(1)

        assertEquals("大阪", viewModel.areaName.value)
    }

    @Test
    fun `loadShopAndImage - areaIdに一致するエリアがない場合はareaNameが空文字になる`() = runTest {
        val shop = createTestShop(areaId = 99)
        fakeLoadShopUseCase.result = shop
        fakeLoadImageUseCase.result = RunStatus.Success(byteArrayOf())

        viewModel.loadShopAndImage(1)

        assertEquals("", viewModel.areaName.value)
    }

    @Test
    fun `loadShopAndImage - Shopがnullの場合はareaNameが更新されない`() = runTest {
        fakeLoadShopUseCase.result = null

        viewModel.loadShopAndImage(1)

        assertEquals("", viewModel.areaName.value)
    }

    // --- addSchedule ---

    @Test
    fun `addSchedule - スケジュール追加後にShopデータを再読み込みする`() = runTest {
        val shop = createTestShop()
        fakeLoadShopUseCase.result = shop
        fakeLoadImageUseCase.result = RunStatus.Success(byteArrayOf())
        val date = LocalDate(2026, 2, 14)

        viewModel.addSchedule(1, date)

        assertEquals(1, fakeUpdateScheduleUseCase.invokedShopId)
        assertEquals(date, fakeUpdateScheduleUseCase.invokedDate)
        // 再読み込みが行われたことを確認
        assertEquals(shop, viewModel.shop.value)
    }

    // --- switchFavorite ---

    @Test
    fun `switchFavorite - お気に入り切り替え後にShopデータを再読み込みする`() = runTest {
        val shop = createTestShop()
        fakeLoadShopUseCase.result = shop
        fakeLoadImageUseCase.result = RunStatus.Success(byteArrayOf())

        viewModel.switchFavorite(true, 1)

        assertEquals(true, fakeSwitchFavoriteUseCase.invokedOnOff)
        assertEquals(1, fakeSwitchFavoriteUseCase.invokedShopId)
        assertEquals(shop, viewModel.shop.value)
    }

    // --- updateStar ---

    @Test
    fun `updateStar - 星評価更新後にShopデータを再読み込みする`() = runTest {
        val shop = createTestShop()
        fakeLoadShopUseCase.result = shop
        fakeLoadImageUseCase.result = RunStatus.Success(byteArrayOf())

        viewModel.updateStar(3, 1)

        assertEquals(1, fakeUpdateStarUseCase.invokedShopId)
        assertEquals(3, fakeUpdateStarUseCase.invokedStar)
        assertEquals(shop, viewModel.shop.value)
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

    private class FakeLoadShopUseCase : LoadShopUseCaseContract {
        var result: Shop? = null
        override suspend fun invoke(id: Int): Shop? = result
    }

    private class FakeLoadImageUseCase : LoadImageUseCaseContract {
        var result: RunStatus<ByteArray> = RunStatus.Idle()
        override suspend fun invoke(name: String): RunStatus<ByteArray> = result
    }

    private class FakeUpdateScheduleInShopUseCase : UpdateScheduleInShopUseCaseContract {
        var invokedShopId: Int? = null
        var invokedDate: LocalDate? = null
        override suspend fun invoke(shopId: Int, date: LocalDate) {
            invokedShopId = shopId
            invokedDate = date
        }
    }

    private class FakeSwitchFavoriteUseCase : SwitchFavoriteUseCaseContract {
        var invokedOnOff: Boolean? = null
        var invokedShopId: Int? = null
        override suspend fun invoke(onOff: Boolean, shopId: Int) {
            invokedOnOff = onOff
            invokedShopId = shopId
        }
    }

    private class FakeUpdateStarUseCase : UpdateStarUseCaseContract {
        var invokedShopId: Int? = null
        var invokedStar: Int? = null
        override suspend fun invoke(shopId: Int, star: Int) {
            invokedShopId = shopId
            invokedStar = star
        }
    }

    private class FakeLoadAreasUseCase : LoadAreasUseCaseContract {
        var result: List<Area> =
            listOf(
                Area(areaId = 1, name = "東京", count = 0, updatedDate = LocalDate(2024, 1, 1), sort = 1)
            )
        override suspend fun invoke(): List<Area> = result
    }
}
