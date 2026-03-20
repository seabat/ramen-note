package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.LocalImageRepositoryContract
import dev.seabat.ramennote.data.repository.ReportsRepositoryContract
import dev.seabat.ramennote.data.repository.ShopsRepositoryContract
import dev.seabat.ramennote.domain.model.Report
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.model.Shop
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DeleteShopAndImageUseCaseTest {

    private val fakeShopsRepository = FakeShopsRepository()
    private val fakeLocalImageRepository = FakeLocalImageRepository()
    private val fakeUpdateShopCountInAreaUseCase = FakeUpdateShopCountInAreaUseCase()
    private val fakeReportsRepository = FakeReportsRepository()

    private val useCase = DeleteShopAndImageUseCase(
        shopsRepository = fakeShopsRepository,
        localAreaImageRepository = fakeLocalImageRepository,
        updateShopCountInAreaUseCase = fakeUpdateShopCountInAreaUseCase,
        reportsRepository = fakeReportsRepository
    )

    // --- 成功ケース ---

    @Test
    fun `invoke - 成功時にRunStatus_Successを返す`() = runTest {
        fakeShopsRepository.shop = createTestShop(id = 1, areaId = 10)

        val result = useCase(1)

        assertIs<RunStatus.Success<String>>(result)
    }

    @Test
    fun `invoke - Shopが存在する場合にreports削除が呼ばれる`() = runTest {
        fakeShopsRepository.shop = createTestShop(id = 1, areaId = 10)

        useCase(1)

        assertEquals(1, fakeReportsRepository.deletedByShopId)
    }

    @Test
    fun `invoke - Shopが存在する場合にshop削除が呼ばれる`() = runTest {
        fakeShopsRepository.shop = createTestShop(id = 1, areaId = 10)

        useCase(1)

        assertEquals(1, fakeShopsRepository.deletedShopId)
    }

    @Test
    fun `invoke - reports削除はshop削除より先に呼ばれる`() = runTest {
        val callOrder = mutableListOf<String>()
        fakeShopsRepository.shop = createTestShop(id = 1, areaId = 10)
        fakeReportsRepository.onDeleteByShopId = { callOrder.add("reports") }
        fakeShopsRepository.onDeleteShopById = { callOrder.add("shop") }

        useCase(1)

        assertEquals(listOf("reports", "shop"), callOrder)
    }

    @Test
    fun `invoke - photoName1が空でない場合に画像削除が呼ばれる`() = runTest {
        fakeShopsRepository.shop = createTestShop(id = 1, areaId = 10, photoName1 = "photo1.jpg")

        useCase(1)

        assertTrue(fakeLocalImageRepository.deletedNames.contains("photo1.jpg"))
    }

    @Test
    fun `invoke - photoName1が空の場合に画像削除が呼ばれない`() = runTest {
        fakeShopsRepository.shop = createTestShop(id = 1, areaId = 10, photoName1 = "")

        useCase(1)

        assertTrue(fakeLocalImageRepository.deletedNames.isEmpty())
    }

    @Test
    fun `invoke - 3枚の画像がすべて削除される`() = runTest {
        fakeShopsRepository.shop = createTestShop(
            id = 1,
            areaId = 10,
            photoName1 = "p1.jpg",
            photoName2 = "p2.jpg",
            photoName3 = "p3.jpg"
        )

        useCase(1)

        assertEquals(listOf("p1.jpg", "p2.jpg", "p3.jpg"), fakeLocalImageRepository.deletedNames)
    }

    @Test
    fun `invoke - 成功時にshopCountInAreaの更新が呼ばれる`() = runTest {
        fakeShopsRepository.shop = createTestShop(id = 1, areaId = 10)

        useCase(1)

        assertEquals(10, fakeUpdateShopCountInAreaUseCase.invokedAreaId)
    }

    // --- Shopが存在しない場合 ---

    @Test
    fun `invoke - Shopが存在しない場合もRunStatus_Successを返す`() = runTest {
        fakeShopsRepository.shop = null

        val result = useCase(999)

        assertIs<RunStatus.Success<String>>(result)
    }

    @Test
    fun `invoke - Shopが存在しない場合はreports削除が呼ばれない`() = runTest {
        fakeShopsRepository.shop = null

        useCase(999)

        assertNull(fakeReportsRepository.deletedByShopId)
    }

    @Test
    fun `invoke - Shopが存在しない場合はshopCountInAreaの更新が呼ばれない`() = runTest {
        fakeShopsRepository.shop = null

        useCase(999)

        assertNull(fakeUpdateShopCountInAreaUseCase.invokedAreaId)
    }

    // --- 例外発生 ---

    @Test
    fun `invoke - 例外発生時にRunStatus_Errorを返す`() = runTest {
        fakeShopsRepository.throwOnGetShopById = true

        val result = useCase(1)

        assertIs<RunStatus.Error<String>>(result)
    }

    // --- ヘルパー ---

    private fun createTestShop(
        id: Int,
        areaId: Int,
        photoName1: String = "",
        photoName2: String = "",
        photoName3: String = ""
    ) = Shop(
        id = id,
        areaId = areaId,
        photoName1 = photoName1,
        photoName2 = photoName2,
        photoName3 = photoName3
    )

    // --- フェイク実装 ---

    private class FakeShopsRepository : ShopsRepositoryContract {
        var shop: Shop? = null
        var deletedShopId: Int? = null
        var throwOnGetShopById = false
        var onDeleteShopById: (() -> Unit)? = null

        override suspend fun getShopById(id: Int): Shop? {
            if (throwOnGetShopById) throw RuntimeException("テスト用例外")
            return shop
        }

        override suspend fun deleteShopById(id: Int) {
            onDeleteShopById?.invoke()
            deletedShopId = id
        }

        override suspend fun getAllShops(): List<Shop> = emptyList()
        override fun getAllShopsFlow(): Flow<List<Shop>> = flowOf(emptyList())
        override suspend fun getShopsByAreaId(areaId: Int): List<Shop> = emptyList()
        override suspend fun insertShop(shop: Shop) = Unit
        override suspend fun updateShop(shop: Shop) = Unit
        override suspend fun deleteShop(shop: Shop) = Unit
        override suspend fun getShopsByName(query: String): List<Shop> = emptyList()
    }

    private class FakeLocalImageRepository : LocalImageRepositoryContract {
        val deletedNames = mutableListOf<String>()

        override suspend fun delete(name: String) {
            deletedNames.add(name)
        }

        override suspend fun save(imageBytes: ByteArray, name: String) = Unit
        override suspend fun load(name: String): RunStatus<ByteArray> = RunStatus.Error("未実装")
        override suspend fun getFilePath(name: String): String? = null
        override suspend fun rename(oldName: String, newName: String) = Unit
    }

    private class FakeUpdateShopCountInAreaUseCase : UpdateShopCountInAreaUseCaseContract {
        var invokedAreaId: Int? = null

        override suspend fun invoke(areaId: Int) {
            invokedAreaId = areaId
        }
    }

    private class FakeReportsRepository : ReportsRepositoryContract {
        var deletedByShopId: Int? = null
        var onDeleteByShopId: (() -> Unit)? = null

        override suspend fun deleteByShopId(shopId: Int) {
            onDeleteByShopId?.invoke()
            deletedByShopId = shopId
        }

        override suspend fun load(): List<Report> = emptyList()
        override suspend fun insert(report: Report) = Unit
        override suspend fun loadById(id: Int): Report? = null
        override suspend fun update(report: Report) = Unit
        override suspend fun delete(id: Int) = Unit
        override suspend fun loadByAreaId(areaId: Int): List<Report> = emptyList()
    }
}
