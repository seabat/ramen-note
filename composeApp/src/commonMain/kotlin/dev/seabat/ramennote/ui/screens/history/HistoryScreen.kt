package dev.seabat.ramennote.ui.screens.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.seabat.ramennote.domain.model.FullReport
import dev.seabat.ramennote.ui.components.AppBar
import dev.seabat.ramennote.ui.components.banner.HintBanner
import dev.seabat.ramennote.ui.components.button.ActionButton
import dev.seabat.ramennote.ui.gallery.SharedImage
import dev.seabat.ramennote.ui.screens.componens.ReportCard
import dev.seabat.ramennote.ui.screens.note.shop.SearchInputField
import dev.seabat.ramennote.ui.share.createRememberedXShareLauncher
import dev.seabat.ramennote.ui.theme.RamenNoteTheme
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import ramennote.composeapp.generated.resources.Res
import ramennote.composeapp.generated.resources.filter_list_24px
import ramennote.composeapp.generated.resources.history_no_data
import ramennote.composeapp.generated.resources.history_search_hint
import ramennote.composeapp.generated.resources.history_year
import ramennote.composeapp.generated.resources.note_notification
import ramennote.composeapp.generated.resources.screen_history_title

@Composable
fun HistoryScreen(
    reportId: Int? = null,
    shopId: Int? = null,
    areaId: Int? = null,
    goToEditReport: (reportId: Int) -> Unit = {},
    clearReportIdParam: () -> Unit = {},
    clearShopParam: () -> Unit = {},
    clearAreaParam: () -> Unit = {},
    initialSearchText: String = "",
    viewModel: HistoryViewModelContract = koinViewModel<HistoryViewModel>()
) {
    val shopNameState by viewModel.shopName.collectAsState()
    val areaNameState by viewModel.areaName.collectAsState()
    val reportsState by viewModel.reports.collectAsState()
    val listState = rememberLazyListState()
    var selectedImageBytes by remember { mutableStateOf<ByteArray?>(null) }
    val xShareLauncher = createRememberedXShareLauncher()
    var isSearchResultVisible by remember { mutableStateOf(initialSearchText.isNotEmpty()) }

    var searchText by remember { mutableStateOf(initialSearchText) }

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

    var isBannerVisible by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        delay(3000)
        isBannerVisible = false
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp, horizontal = 16.dp)
    ) {
        AppBar(
            title =
                when {
                    areaNameState.isNotEmpty() ->
                        "${stringResource(Res.string.screen_history_title)}($areaNameState)"
                    shopNameState.isNotEmpty() ->
                        "${stringResource(Res.string.screen_history_title)}($shopNameState)"
                    else -> stringResource(Res.string.screen_history_title)
                }
        )
        if (reportsState.isNotEmpty()) {
            Menu(
                searchText = searchText,
                onFilterClick = {},
                onSearchTextChange = { text ->
                    searchText = text
                    isSearchResultVisible = text.isNotEmpty()
                    if (text.isNotEmpty()) {
                        // TODO:
                    }
                }
            )
            Spacer(modifier = Modifier.height(4.dp))
            HintBanner(
                isVisible = isBannerVisible,
                text = stringResource(Res.string.note_notification)
            )
            Box {
                // レポート一覧
                ReportsList(
                    reports = reportsState,
                    listState = listState,
                    goToEditReport = goToEditReport,
                    onImageTap = { imageBytes -> selectedImageBytes = imageBytes },
                    onShareTap = { postText, imageBytes ->
                        viewModel.shareToX(
                            postText = postText,
                            image =
                                imageBytes?.let {
                                    SharedImage(it)
                                },
                            xShareLauncher = xShareLauncher
                        )
                    }
                )
            }

            // reportIdが指定されている場合、該当アイテムまで自動スクロール
            LaunchedEffect(reportId, reportsState) {
                if (reportId != null && reportsState.isNotEmpty()) {
                    // レポートを年月でグループ化し、ソート
                    val grouped = groupReports(reportsState)

                    // 該当のreportIdのインデックスを探す
                    var targetIndex = -1
                    var currentIndex = 0
                    loop@ for ((_, monthReports) in grouped) {
                        currentIndex++ // 年月ヘッダーのインデックス
                        for (report in monthReports) {
                            if (report.id == reportId) {
                                targetIndex = currentIndex
                                break@loop
                            }
                            currentIndex++
                        }
                    }

                    // 見つかった場合、スクロール
                    if (targetIndex >= 0) {
                        // 少し遅延を入れてレイアウトが完了してからスクロール
                        delay(500)
                        listState.animateScrollToItem(targetIndex)
                    }

                    clearReportIdParam()
                }
            }

            // 画像ダイアログ
            selectedImageBytes?.let { imageBytes ->
                ReportImageDialog(
                    imageBytes = imageBytes,
                    onDismiss = { selectedImageBytes = null }
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
}

@Preview
@Composable
fun HistoryScreenPreview() {
    RamenNoteTheme {
        HistoryScreen(viewModel = MockHistoryViewModel())
    }
}

@Composable
private fun Menu(
    searchText: String,
    onFilterClick: () -> Unit = {},
    onSearchTextChange: (String) -> Unit
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 0.dp, vertical = 4.dp),
//        horizontalArrangement = Arrangement.spacedBy(8.dp)
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        YearButton(onClick = onFilterClick)
        Spacer(modifier = Modifier.width(16.dp))
        SearchInputField(
            placeholder = stringResource(Res.string.history_search_hint),
            value = searchText,
            onValueChange = onSearchTextChange
        )
    }
}

@Composable
private fun YearButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ActionButton(
        icon = vectorResource(Res.drawable.filter_list_24px),
        text = stringResource(Res.string.history_year),
        onClick = onClick,
        modifier = modifier
    )
}

@Composable
private fun ReportsList(
    reports: List<FullReport>,
    listState: LazyListState,
    goToEditReport: (Int) -> Unit,
    onImageTap: (ByteArray?) -> Unit,
    onShareTap: (String, ByteArray?) -> Unit
) {


    // グルーピング: 年月ごと (YYYY-MM)
    val grouped = groupReports(reports)

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
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
                    onImageTap = { onImageTap(report.imageBytes) },
                    onTap = {},
                    onShareTap = onShareTap
                )
            }
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
