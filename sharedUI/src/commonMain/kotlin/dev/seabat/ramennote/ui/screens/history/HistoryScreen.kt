package dev.seabat.ramennote.ui.screens.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.seabat.ramennote.domain.model.FullReport
import dev.seabat.ramennote.domain.model.Shop
import dev.seabat.ramennote.domain.util.createTodayLocalDate
import dev.seabat.ramennote.ui.components.AppBar
import dev.seabat.ramennote.ui.components.banner.HintBanner
import dev.seabat.ramennote.ui.components.button.AddFab
import dev.seabat.ramennote.ui.components.dialog.SelectShopDialog
import dev.seabat.ramennote.ui.components.dropdown.DropdownAnchorField
import dev.seabat.ramennote.ui.screens.componens.ReportCard
import dev.seabat.ramennote.ui.screens.componens.ShopItem
import dev.seabat.ramennote.ui.screens.note.shop.SearchInputField
import dev.seabat.ramennote.ui.share.createRememberedXShareLauncher
import dev.seabat.ramennote.ui.theme.RamenNoteTheme
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import ramennote.sharedui.generated.resources.Res
import ramennote.sharedui.generated.resources.history_no_data
import ramennote.sharedui.generated.resources.history_search_hint
import ramennote.sharedui.generated.resources.history_year
import ramennote.sharedui.generated.resources.history_year_all
import ramennote.sharedui.generated.resources.note_hit_count
import ramennote.sharedui.generated.resources.note_notification
import ramennote.sharedui.generated.resources.screen_history_title
import ramennote.sharedui.generated.resources.select_shop_dialog_write_report_button

@Composable
fun HistoryScreen(
    reportId: Int? = null,
    shopId: Int? = null,
    areaId: Int? = null,
    goToEditReport: (reportId: Int) -> Unit = {},
    goToReport: (shopId: Int, shopName: String, menuName: String, iso8601Date: String) -> Unit = { _, _, _, _ -> },
    clearReportIdParam: () -> Unit = {},
    clearShopParam: () -> Unit = {},
    clearAreaParam: () -> Unit = {},
    initialSearchText: String = "",
    viewModel: HistoryViewModelContract = koinViewModel<HistoryViewModel>()
) {
    val shopNameState by viewModel.shopName.collectAsState()
    val areaNameState by viewModel.areaName.collectAsState()
    val reportsState by viewModel.reports.collectAsState()
    val shops by viewModel.shops.collectAsState()
    val yearState by viewModel.year.collectAsState()
    val yearsState by viewModel.selectableYears.collectAsState()

    val listState = rememberLazyListState()
    var selectedImagePath by remember { mutableStateOf<String?>(null) }
    var showSelectShopDialog by remember { mutableStateOf(false) }
    val xShareLauncher = createRememberedXShareLauncher()
    // ReportList 再生成時に渡す初期値（店舗フィルター適用後も検索テキストを保持するため）
    var listSearchText by remember { mutableStateOf(initialSearchText) }
    var listIsSearchResultVisible by remember { mutableStateOf(initialSearchText.isNotEmpty()) }

    LaunchedEffect(Unit) {
        when {
            areaId != null -> {
                viewModel.loadReportsByArea(areaId)
                clearAreaParam()
            }
            shopId != null -> {
                viewModel.loadReportsByShop(shopId)
                clearShopParam()
            }
            else -> viewModel.loadReports()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            AppBar(
                title =
                    when {
                        areaNameState.isNotEmpty() ->
                            "${stringResource(Res.string.screen_history_title)}($areaNameState)"
                        shopNameState.isNotEmpty() ->
                            "${stringResource(Res.string.screen_history_title)}($shopNameState)"
                        yearState.isNotEmpty() ->
                            "${stringResource(Res.string.screen_history_title)}($yearState)"
                        else -> stringResource(Res.string.screen_history_title)
                    }
            )
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    if (reportsState.isNotEmpty()) {
                        Box {
                            // レポート一覧
                            ReportList(
                                reports = reportsState,
                                listState = listState,
                                searchText = listSearchText,
                                isSearchResultVisible = listIsSearchResultVisible,
                                shops = shops,
                                years = yearsState,
                                selectedYear = yearState,
                                goToEditReport = goToEditReport,
                                onDisplayImage = { imagePath -> selectedImagePath = imagePath },
                                onFilter = { shop ->
                                    // フィルター適用後も検索フィールドに店舗名を表示し、検索結果は非表示にする
                                    listSearchText = shop.name
                                    listIsSearchResultVisible = false
                                    viewModel.loadReportsByShop(shop.id)
                                },
                                onShare = { postText, photoName ->
                                    viewModel.shareToX(
                                        postText = postText,
                                        photoName = photoName,
                                        xShareLauncher = xShareLauncher
                                    )
                                },
                                onSearchTextChange = { text ->
                                    listSearchText = text
                                    if (text.isNotEmpty()) {
                                        listIsSearchResultVisible = true
                                        viewModel.searchShops(text)
                                    } else {
                                        listIsSearchResultVisible = false
                                        viewModel.loadReports()
                                    }
                                },
                                onYearSelect = { year ->
                                    listSearchText = ""
                                    listIsSearchResultVisible = false
                                    viewModel.loadReportsByYear(year)
                                },
                                onClearYearFilter = { viewModel.loadReports() }
                            )
                        }

                        // reportIdが指定されている場合、該当アイテムまで自動スクロール
                        // キーを reportId のみにして、レポートが1件ずつ追加されるたびに
                        // LaunchedEffect が再起動・キャンセルされる問題を防ぐ
                        LaunchedEffect(reportId) {
                            val id = reportId ?: return@LaunchedEffect

                            // 全件読み込み完了を待つ（reportsStateが変化しなくなるまで）
                            var lastSize = -1
                            while (reportsState.size != lastSize) {
                                lastSize = reportsState.size
                                delay(100)
                            }
                            if (reportsState.isEmpty()) {
                                clearReportIdParam()
                                return@LaunchedEffect
                            }

                            // レポートを年月でグループ化し、ソート
                            val grouped = groupReports(reportsState)

                            // 該当のreportIdのインデックスを探す
                            // LazyColumnの構造: Menu(0) + HintBanner(1) + グループヘッダー + レポートアイテム
                            var targetIndex = -1
                            var currentIndex = 2 // Menu と HintBanner の2アイテム分をオフセット
                            loop@ for ((_, monthReports) in grouped) {
                                // currentIndex はヘッダーの位置（スキップ）
                                for (report in monthReports) {
                                    currentIndex++ // レポートの位置へ移動してから確認
                                    if (report.id == id) {
                                        targetIndex = currentIndex
                                        break@loop
                                    }
                                }
                                currentIndex++ // 次のグループのヘッダー位置へ移動
                            }

                            // 見つかった場合、スクロール
                            if (targetIndex >= 0) {
                                // 少し遅延を入れてレイアウトが完了してからスクロール
                                delay(300)
                                listState.animateScrollToItem(targetIndex)
                            }

                            clearReportIdParam()
                        }

                        // 画像ダイアログ
                        selectedImagePath?.let { path ->
                            ReportImageDialog(
                                model = path,
                                onDismiss = { selectedImagePath = null }
                            )
                        }
                    } else {
                        Text(
                            text = stringResource(Res.string.history_no_data),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
                if (!showSelectShopDialog) {
                    AddFab(
                        modifier = Modifier.align(Alignment.BottomEnd),
                        contentDescription = "食レポを追加",
                        onAddClick = { showSelectShopDialog = true }
                    )
                }
            }
        }
        if (showSelectShopDialog) {
            SelectShopDialog(
                onDismiss = { showSelectShopDialog = false },
                confirmButtonLabel = stringResource(Res.string.select_shop_dialog_write_report_button),
                onConfirmClick = { shop ->
                    showSelectShopDialog = false
                    goToReport(shop.id, shop.name, shop.menuName1, createTodayLocalDate().toString())
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun YearDropdownField(
    years: List<Int>,
    selectedYear: String,
    onYearSelect: (Int) -> Unit,
    onClearYearFilter: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val allLabel = stringResource(Res.string.history_year_all)
    val yearHint = stringResource(Res.string.history_year)

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        DropdownAnchorField(
            text = if (selectedYear.isNotEmpty()) selectedYear else yearHint,
            expanded = expanded,
            onToggle = { expanded = !expanded },
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            // 「全て」選択肢
            DropdownMenuItem(
                text = { Text(allLabel) },
                onClick = {
                    onClearYearFilter()
                    expanded = false
                }
            )
            years.forEach { year ->
                DropdownMenuItem(
                    text = { Text(year.toString()) },
                    onClick = {
                        onYearSelect(year)
                        expanded = false
                    }
                )
            }
        }
    }
}

/**
 * 食レポ一覧を表示するComposable。
 *
 * 検索バーと年フィルターボタンを含むMenuを上部に表示し、
 * 検索中は店舗一覧、それ以外は年月ごとにグルーピングされた食レポ一覧を表示する。
 * 一覧の先頭にはHintBannerを3秒間表示する。
 *
 * @param reports 表示する食レポのリスト
 * @param listState LazyColumnのスクロール状態
 * @param searchText 初期表示時の検索テキスト
 * @param shops 検索結果として表示する店舗のリスト
 * @param goToEditReport 食レポ編集画面へ遷移するコールバック（reportIdを渡す）
 * @param onDisplayImage 画像を拡大表示させるコールバック（画像バイト列を渡す）
 * @param onFilter 食レポ一覧を店舗でフィルタリングするコールバック（Shop を渡す）
 * @param onShare シェアボタンタップ時のコールバック（テキストと画像バイト列を渡す）
 * @param onSearchTextChange 検索テキスト変更時のコールバック（空文字でフィルター解除）
 */
@Composable
private fun ReportList(
    reports: List<FullReport>,
    listState: LazyListState,
    searchText: String,
    isSearchResultVisible: Boolean,
    shops: List<Shop>,
    years: List<Int>,
    selectedYear: String,
    goToEditReport: (Int) -> Unit,
    onDisplayImage: (String?) -> Unit,
    onFilter: (Shop) -> Unit = {},
    onShare: (String, String) -> Unit,
    onSearchTextChange: (String) -> Unit = {},
    onYearSelect: (Int) -> Unit = {},
    onClearYearFilter: () -> Unit = {}
) {
    var isBannerVisible by remember { mutableStateOf(true) }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        delay(3000)
        isBannerVisible = false
    }

    // グルーピング: 年月ごと (YYYY-MM)
    val grouped = groupReports(reports)

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Menu(
                searchText = searchText,
                years = years,
                selectedYear = selectedYear,
                onYearSelect = onYearSelect,
                onClearYearFilter = onClearYearFilter,
                onSearchTextChange = onSearchTextChange
            )
        }
        if (isSearchResultVisible) {
            item {
                Column {
                    Text(
                        text = stringResource(Res.string.note_hit_count, shops.size),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    if (shops.isNotEmpty()) {
                        HorizontalDivider(
                            Modifier,
                            1.dp,
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        )
                    }
                }
            }
            items(shops) { shop ->
                ShopItem(
                    shop = shop,
                    onShopClick = {
                        keyboardController?.hide()
                        onFilter(shop)
                    },
                    onDelete = { /* 削除処理 */ }
                )
            }
        } else {
            item {
                HintBanner(
                    isVisible = isBannerVisible,
                    text = stringResource(Res.string.note_notification)
                )
            }
            grouped.forEach { (yearMonth, monthReports) ->
                item {
                    Text(
                        text = yearMonth,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(monthReports) { report ->
                    ReportCard(
                        report = report,
                        isSimpleDisplay = false,
                        onLongPress = { goToEditReport(report.id) },
                        onImageTap = { onDisplayImage(report.imagePath) },
                        onTap = {},
                        onShareTap = onShare
                    )
                }
            }
        }
    }
}

@Composable
private fun Menu(
    searchText: String,
    years: List<Int>,
    selectedYear: String,
    onYearSelect: (Int) -> Unit,
    onClearYearFilter: () -> Unit,
    onSearchTextChange: (String) -> Unit
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 0.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        YearDropdownField(
            years = years,
            selectedYear = selectedYear,
            onYearSelect = onYearSelect,
            onClearYearFilter = onClearYearFilter,
            modifier = Modifier.width(100.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Box(modifier = Modifier.weight(1f)) {
            SearchInputField(
                placeholder = stringResource(Res.string.history_search_hint),
                value = searchText,
                onValueChange = onSearchTextChange
            )
        }
    }
}

private fun groupReports(reports: List<FullReport>): Map<String, List<FullReport>> =
    reports
        .sortedByDescending { it.date }
        .groupBy { report ->
            val date = report.date
            "${date.year}-${date.monthNumber.toString().padStart(2, '0')}"
        }.filterKeys { it.isNotEmpty() }

@Preview
@Composable
private fun MenuPreview() {
    RamenNoteTheme {
        Menu(
            searchText = "",
            years = listOf(2025, 2024),
            selectedYear = "",
            onYearSelect = {},
            onClearYearFilter = {},
            onSearchTextChange = {}
        )
    }
}

@Preview
@Composable
private fun MenuWithSearchTextPreview() {
    RamenNoteTheme {
        Menu(
            searchText = "麺屋",
            years = listOf(2025, 2024),
            selectedYear = "",
            onYearSelect = {},
            onClearYearFilter = {},
            onSearchTextChange = {}
        )
    }
}

@Preview
@Composable
fun HistoryScreenPreview() {
    RamenNoteTheme {
        HistoryScreen(viewModel = MockHistoryViewModel())
    }
}

@Preview
@Composable
fun HistoryScreenForSearchWithNoHitPreview() {
    RamenNoteTheme {
        HistoryScreen(
            initialSearchText = "一風堂",
            viewModel = MockHistoryViewModel()
        )
    }
}

@Preview
@Composable
fun HistoryScreenForSearchWithHitsPreview() {
    RamenNoteTheme {
        HistoryScreen(
            initialSearchText = "一風堂",
            viewModel = MockHistoryViewModelWithSearchHits()
        )
    }
}
