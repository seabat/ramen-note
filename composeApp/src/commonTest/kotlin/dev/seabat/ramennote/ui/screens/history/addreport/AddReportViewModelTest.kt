package dev.seabat.ramennote.ui.screens.history.addreport

import dev.seabat.ramennote.domain.model.Report
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.model.Shop
import dev.seabat.ramennote.domain.usecase.AddReportUseCaseContract
import dev.seabat.ramennote.domain.usecase.CreateReportPhotoNameUseCaseContract
import dev.seabat.ramennote.domain.usecase.LoadShopUseCaseContract
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

@OptIn(ExperimentalCoroutinesApi::class)
class AddReportViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val fakeCreateReportPhotoNameUseCase = FakeCreateReportPhotoNameUseCase()
    private val fakeAddReportUseCase = FakeAddReportUseCase()
    private val fakeLoadShopUseCase = FakeLoadShopUseCase()

    private lateinit var viewModel: AddReportViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel =
            AddReportViewModel(
                createReportPhotoNameUseCase = fakeCreateReportPhotoNameUseCase,
                addReportUseCase = fakeAddReportUseCase,
                loadShopUseCase = fakeLoadShopUseCase
            )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // --- 初期状態 ---

    @Test
    fun `reportedStatus - 初期状態はIdle`() {
        assertIs<RunStatus.Idle<Int>>(viewModel.reportedStatus.value)
    }

    // --- report ---

    @Test
    fun `report - 成功時にreportedStatusがSuccessになる`() = runTest {
        fakeAddReportUseCase.result = RunStatus.Success(1)

        viewModel.report(
            menuName = "醤油らーめん",
            reportedDate = LocalDate(2024, 9, 1),
            impression = "美味しかった",
            shopId = 1,
            image = null,
            star = 4
        )

        assertIs<RunStatus.Success<Int>>(viewModel.reportedStatus.value)
        assertEquals(1, (viewModel.reportedStatus.value as RunStatus.Success).data)
    }

    @Test
    fun `report - addReportUseCaseに正しいReportが渡される`() = runTest {
        fakeCreateReportPhotoNameUseCase.result = "photo_001.jpg"
        fakeAddReportUseCase.result = RunStatus.Success(1)

        viewModel.report(
            menuName = "つけ麺",
            reportedDate = LocalDate(2024, 9, 15),
            impression = "麺が太くて美味",
            shopId = 5,
            image = null,
            star = 5
        )

        val report = fakeAddReportUseCase.invokedReport
        assertEquals(0, report?.id) // id は後で更新される
        assertEquals(5, report?.shopId)
        assertEquals("つけ麺", report?.menuName)
        assertEquals("photo_001.jpg", report?.photoName)
        assertEquals("麺が太くて美味", report?.impression)
        assertEquals(LocalDate(2024, 9, 15), report?.date)
        assertEquals(5, report?.star)
    }

    @Test
    fun `report - shopIdからareaIdを取得してReportにセットする`() = runTest {
        fakeLoadShopUseCase.result = Shop(id = 3, name = "テスト店", areaId = 7)
        fakeAddReportUseCase.result = RunStatus.Success(1)

        viewModel.report(
            menuName = "塩らーめん",
            reportedDate = LocalDate(2024, 9, 1),
            impression = "",
            shopId = 3,
            image = null,
            star = 3
        )

        assertEquals(7, fakeAddReportUseCase.invokedReport?.areaId)
    }

    @Test
    fun `report - shopが見つからない場合はareaIdが0になる`() = runTest {
        fakeLoadShopUseCase.result = null
        fakeAddReportUseCase.result = RunStatus.Success(1)

        viewModel.report(
            menuName = "味噌らーめん",
            reportedDate = LocalDate(2024, 9, 1),
            impression = "",
            shopId = 99,
            image = null,
            star = 3
        )

        assertEquals(0, fakeAddReportUseCase.invokedReport?.areaId)
    }

    @Test
    fun `report - addReportUseCaseがErrorを返した場合reportedStatusがErrorになる`() = runTest {
        fakeAddReportUseCase.result = RunStatus.Error("保存失敗")

        viewModel.report(
            menuName = "らーめん",
            reportedDate = LocalDate(2024, 9, 1),
            impression = "",
            shopId = 1,
            image = null,
            star = 3
        )

        val state = viewModel.reportedStatus.value
        assertIs<RunStatus.Error<Int>>(state)
        assertEquals("保存失敗", state.message)
    }

    @Test
    fun `report - UseCaseが例外をスローした場合reportedStatusがErrorになる`() = runTest {
        fakeAddReportUseCase.shouldThrow = true

        viewModel.report(
            menuName = "らーめん",
            reportedDate = LocalDate(2024, 9, 1),
            impression = "",
            shopId = 1,
            image = null,
            star = 3
        )

        assertIs<RunStatus.Error<Int>>(viewModel.reportedStatus.value)
    }

    @Test
    fun `report - imageがnullの場合addReportUseCaseにnullが渡される`() = runTest {
        fakeAddReportUseCase.result = RunStatus.Success(1)

        viewModel.report(
            menuName = "らーめん",
            reportedDate = LocalDate(2024, 9, 1),
            impression = "",
            shopId = 1,
            image = null,
            star = 3
        )

        assertEquals(null, fakeAddReportUseCase.invokedImageBytes)
    }

    @Test
    fun `report - createReportPhotoNameUseCaseが返すphotoNameがReportに使われる`() = runTest {
        fakeCreateReportPhotoNameUseCase.result = "unique_photo_name.jpg"
        fakeAddReportUseCase.result = RunStatus.Success(1)

        viewModel.report(
            menuName = "らーめん",
            reportedDate = LocalDate(2024, 9, 1),
            impression = "",
            shopId = 1,
            image = null,
            star = 3
        )

        assertEquals("unique_photo_name.jpg", fakeAddReportUseCase.invokedReport?.photoName)
    }

    // --- setReportedStatusToIdle ---

    @Test
    fun `setReportedStatusToIdle - reportedStatusがIdleに戻る`() = runTest {
        fakeAddReportUseCase.result = RunStatus.Success(1)
        viewModel.report(
            menuName = "らーめん",
            reportedDate = LocalDate(2024, 9, 1),
            impression = "",
            shopId = 1,
            image = null,
            star = 3
        )
        assertIs<RunStatus.Success<Int>>(viewModel.reportedStatus.value)

        viewModel.setReportedStatusToIdle()

        assertIs<RunStatus.Idle<Int>>(viewModel.reportedStatus.value)
    }

    // --- フェイク UseCase 実装 ---

    private class FakeCreateReportPhotoNameUseCase : CreateReportPhotoNameUseCaseContract {
        var result: String = "photo.jpg"
        override fun invoke(): String = result
    }

    private class FakeAddReportUseCase : AddReportUseCaseContract {
        var invokedReport: Report? = null
        var invokedImageBytes: ByteArray? = null
        var result: RunStatus<Int> = RunStatus.Success(1)
        var shouldThrow = false

        override suspend fun invoke(report: Report, imageBytes: ByteArray?): RunStatus<Int> {
            if (shouldThrow) throw RuntimeException("保存エラー")
            invokedReport = report
            invokedImageBytes = imageBytes
            return result
        }
    }

    private class FakeLoadShopUseCase : LoadShopUseCaseContract {
        var result: Shop? = Shop(id = 1, name = "テスト店", areaId = 1)
        override suspend fun invoke(id: Int): Shop? = result
    }
}
