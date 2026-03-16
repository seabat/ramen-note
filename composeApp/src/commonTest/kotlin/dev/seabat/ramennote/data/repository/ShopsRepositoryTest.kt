package dev.seabat.ramennote.data.repository

import androidx.room.InvalidationTracker
import dev.seabat.ramennote.data.database.RamenNoteDatabase
import dev.seabat.ramennote.data.database.dao.ShopDao
import dev.seabat.ramennote.data.database.entity.ShopEntity
import dev.seabat.ramennote.domain.model.Shop
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ShopsRepositoryTest {

    // --- getAllShops ---

    @Test
    fun `getAllShops - DAOが返したエンティティをドメインモデルに変換して返す`() = runTest {
        val entities =
            listOf(
                createEntity(id = 1, name = "麺屋東京", areaId = 1),
                createEntity(id = 2, name = "らーめん大阪", areaId = 2)
            )
        val repository = buildRepository(FakeShopDao(entities.toMutableList()))

        val result = repository.getAllShops()

        assertEquals(2, result.size)
        assertEquals(1, result[0].id)
        assertEquals("麺屋東京", result[0].name)
        assertEquals(1, result[0].areaId)
        assertEquals(2, result[1].id)
        assertEquals("らーめん大阪", result[1].name)
    }

    @Test
    fun `getAllShops - DAOがエンティティを返さない場合は空リストを返す`() = runTest {
        val repository = buildRepository(FakeShopDao())

        val result = repository.getAllShops()

        assertTrue(result.isEmpty())
    }

    // --- getAllShopsFlow ---

    @Test
    fun `getAllShopsFlow - Flowでエンティティをドメインモデルに変換して返す`() = runTest {
        val entities =
            listOf(
                createEntity(id = 1, name = "麺屋東京", areaId = 1),
                createEntity(id = 2, name = "らーめん大阪", areaId = 2)
            )
        val repository = buildRepository(FakeShopDao(entities.toMutableList()))

        val result = repository.getAllShopsFlow().first()

        assertEquals(2, result.size)
        assertEquals("麺屋東京", result[0].name)
        assertEquals("らーめん大阪", result[1].name)
    }

    // --- getShopById ---

    @Test
    fun `getShopById - 一致する店舗をドメインモデルに変換して返す`() = runTest {
        val entity = createEntity(id = 42, name = "福岡ラーメン", areaId = 3)
        val repository = buildRepository(FakeShopDao(mutableListOf(entity)))

        val result = repository.getShopById(42)

        assertNotNull(result)
        assertEquals(42, result.id)
        assertEquals("福岡ラーメン", result.name)
        assertEquals(3, result.areaId)
    }

    @Test
    fun `getShopById - 一致するIDがない場合はnullを返す`() = runTest {
        val repository = buildRepository(FakeShopDao())

        val result = repository.getShopById(999)

        assertNull(result)
    }

    // --- getShopsByAreaId ---

    @Test
    fun `getShopsByAreaId - areaIdが一致する店舗リストを返す`() = runTest {
        val entities =
            listOf(
                createEntity(id = 1, name = "東京A", areaId = 1),
                createEntity(id = 2, name = "東京B", areaId = 1),
                createEntity(id = 3, name = "大阪A", areaId = 2)
            )
        val repository = buildRepository(FakeShopDao(entities.toMutableList()))

        val result = repository.getShopsByAreaId(1)

        assertEquals(2, result.size)
        assertTrue(result.all { it.areaId == 1 })
    }

    @Test
    fun `getShopsByAreaId - 一致するエリアがない場合は空リストを返す`() = runTest {
        val repository = buildRepository(FakeShopDao())

        val result = repository.getShopsByAreaId(99)

        assertTrue(result.isEmpty())
    }

    // --- insertShop ---

    @Test
    fun `insertShop - ドメインモデルをエンティティに変換してDAOに渡す`() = runTest {
        val dao = FakeShopDao()
        val repository = buildRepository(dao)
        val shop = createShop(id = 10, name = "新規店舗", areaId = 2)

        repository.insertShop(shop)

        val inserted = dao.insertedShop
        assertNotNull(inserted)
        assertEquals(10, inserted.id)
        assertEquals("新規店舗", inserted.name)
        assertEquals(2, inserted.areaId)
    }

    // --- updateShop ---

    @Test
    fun `updateShop - ドメインモデルをエンティティに変換してDAOに渡す`() = runTest {
        val entity = createEntity(id = 5, name = "旧店名", areaId = 1)
        val dao = FakeShopDao(mutableListOf(entity))
        val repository = buildRepository(dao)
        val shop = createShop(id = 5, name = "新店名", areaId = 1)

        repository.updateShop(shop)

        assertEquals("新店名", dao.updatedShop?.name)
        assertEquals(5, dao.updatedShop?.id)
    }

    // --- deleteShop ---

    @Test
    fun `deleteShop - ドメインモデルをエンティティに変換してDAOに渡す`() = runTest {
        val entity = createEntity(id = 7, name = "削除対象店", areaId = 1)
        val dao = FakeShopDao(mutableListOf(entity))
        val repository = buildRepository(dao)
        val shop = createShop(id = 7, name = "削除対象店", areaId = 1)

        repository.deleteShop(shop)

        assertEquals(7, dao.deletedShop?.id)
    }

    // --- deleteShopById ---

    @Test
    fun `deleteShopById - IDがDAOに渡される`() = runTest {
        val entity = createEntity(id = 3, name = "削除店", areaId = 1)
        val dao = FakeShopDao(mutableListOf(entity))
        val repository = buildRepository(dao)

        repository.deleteShopById(3)

        assertEquals(3, dao.deletedById)
    }

    // --- getShopsByName ---

    @Test
    fun `getShopsByName - クエリに一致する店舗リストを返す`() = runTest {
        val entities =
            listOf(
                createEntity(id = 1, name = "東京ラーメン", areaId = 1),
                createEntity(id = 2, name = "大阪つけ麺", areaId = 2),
                createEntity(id = 3, name = "東京つけ麺", areaId = 1)
            )
        val repository = buildRepository(FakeShopDao(entities.toMutableList()))

        val result = repository.getShopsByName("東京")

        assertEquals(2, result.size)
        assertTrue(result.all { it.name.contains("東京") })
    }

    @Test
    fun `getShopsByName - クエリに一致する店舗がない場合は空リストを返す`() = runTest {
        val repository = buildRepository(FakeShopDao())

        val result = repository.getShopsByName("存在しない")

        assertTrue(result.isEmpty())
    }

    // --- Entity ↔ Domain 変換 ---

    @Test
    fun `toDomainModel - scheduledDateが空文字の場合はnullに変換される`() = runTest {
        val entity = createEntity(id = 1, scheduledDate = "")
        val repository = buildRepository(FakeShopDao(mutableListOf(entity)))

        val result = repository.getShopById(1)

        assertNull(result?.scheduledDate)
    }

    @Test
    fun `toDomainModel - scheduledDateが日付文字列の場合はLocalDateに変換される`() = runTest {
        val entity = createEntity(id = 1, scheduledDate = "2024-09-15")
        val repository = buildRepository(FakeShopDao(mutableListOf(entity)))

        val result = repository.getShopById(1)

        assertEquals(LocalDate.parse("2024-09-15"), result?.scheduledDate)
    }

    @Test
    fun `toEntity - scheduledDateがnullの場合は空文字に変換される`() = runTest {
        val dao = FakeShopDao()
        val repository = buildRepository(dao)
        val shop = createShop(id = 1, scheduledDate = null)

        repository.insertShop(shop)

        assertEquals("", dao.insertedShop?.scheduledDate)
    }

    @Test
    fun `toEntity - scheduledDateがLocalDateの場合は文字列に変換される`() = runTest {
        val dao = FakeShopDao()
        val repository = buildRepository(dao)
        val shop = createShop(id = 1, scheduledDate = LocalDate.parse("2024-09-15"))

        repository.insertShop(shop)

        assertEquals("2024-09-15", dao.insertedShop?.scheduledDate)
    }

    // --- ヘルパー ---

    private fun buildRepository(dao: FakeShopDao): ShopsRepository =
        ShopsRepository(FakeRamenNoteDatabase(dao))

    private fun createEntity(
        id: Int = 1,
        name: String = "テスト店",
        areaId: Int = 1,
        scheduledDate: String = ""
    ) = ShopEntity(
        id = id,
        name = name,
        areaId = areaId,
        shopUrl = "",
        mapUrl = "",
        star = 0,
        stationName = "",
        category = "",
        scheduledDate = scheduledDate,
        menuName1 = "",
        menuName2 = "",
        menuName3 = "",
        photoName1 = "",
        photoName2 = "",
        photoName3 = "",
        description1 = "",
        description2 = "",
        description3 = "",
        favorite = false,
        note = ""
    )

    private fun createShop(
        id: Int = 1,
        name: String = "テスト店",
        areaId: Int = 1,
        scheduledDate: LocalDate? = null
    ) = Shop(
        id = id,
        name = name,
        areaId = areaId,
        scheduledDate = scheduledDate
    )

    // --- フェイク実装 ---

    private class FakeShopDao(
        private val shops: MutableList<ShopEntity> = mutableListOf()
    ) : ShopDao {
        var insertedShop: ShopEntity? = null
        var updatedShop: ShopEntity? = null
        var deletedShop: ShopEntity? = null
        var deletedById: Int? = null

        override suspend fun getAllShops(): List<ShopEntity> = shops

        override fun getAllShopsFlow(): Flow<List<ShopEntity>> = flowOf(shops)

        override suspend fun getShopById(id: Int): ShopEntity? =
            shops.find { it.id == id }

        override suspend fun getShopsByAreaId(areaId: Int): List<ShopEntity> =
            shops.filter { it.areaId == areaId }

        override suspend fun insertShop(shop: ShopEntity) {
            insertedShop = shop
            shops.add(shop)
        }

        override suspend fun updateShop(shop: ShopEntity) {
            updatedShop = shop
            val index = shops.indexOfFirst { it.id == shop.id }
            if (index >= 0) shops[index] = shop
        }

        override suspend fun deleteShop(shop: ShopEntity) {
            deletedShop = shop
            shops.removeAll { it.id == shop.id }
        }

        override suspend fun deleteShopById(id: Int) {
            deletedById = id
            shops.removeAll { it.id == id }
        }

        override suspend fun searchShopsByName(query: String): List<ShopEntity> =
            shops.filter { it.name.contains(query) }
    }

    private class FakeRamenNoteDatabase(private val dao: FakeShopDao) : RamenNoteDatabase() {
        override fun areaDao() = throw UnsupportedOperationException("テスト対象外")
        override fun shopDao() = dao
        override fun reportDao() = throw UnsupportedOperationException("テスト対象外")
        override fun createInvalidationTracker(): InvalidationTracker =
            InvalidationTracker(this, emptyMap(), emptyMap())
    }
}
