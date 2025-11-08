package dev.seabat.ramennote.ui.screens.history

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.seabat.ramennote.domain.model.FullReport
import dev.seabat.ramennote.ui.components.AppBar
import dev.seabat.ramennote.ui.theme.RamenNoteTheme
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import ramennote.composeapp.generated.resources.Res
import ramennote.composeapp.generated.resources.history_no_data
import ramennote.composeapp.generated.resources.screen_history_title

@Composable
fun HistoryScreen(
    reportId: Int? = null,
    goToEditReport: (reportId: Int) -> Unit = {},
    clearReportId: () -> Unit = {},
    viewModel: HistoryViewModelContract = koinViewModel<HistoryViewModel>()
) {
    val reports by viewModel.reports.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) { viewModel.loadReports() }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp)
    ) {
        AppBar(title = stringResource(Res.string.screen_history_title))
        if (reports.isNotEmpty()) {
            Box {
                // レポート一覧
                ReportsList(
                    reports = reports,
                    listState = listState,
                    goToEditReport = goToEditReport
                )

                // 起動時に右上へ5秒間表示しフェードアウトするヒント
                val showHint = remember { mutableStateOf(true) }
                LaunchedEffect(Unit) {
                    delay(5000)
                    showHint.value = false
                }

                androidx.compose.animation.AnimatedVisibility(
                    modifier = Modifier.align(Alignment.TopEnd),
                    visible = showHint.value,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = "カードを長押しすると編集できます",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            // reportIdが指定されている場合、該当アイテムまで自動スクロール
            LaunchedEffect(reportId, reports) {
                if (reportId != null && reports.isNotEmpty()) {
                    // レポートを年月でグループ化し、ソート
                    val grouped = groupReports(reports)

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

                    // reportIdを処理した後、Stateをクリア
                    clearReportId()
                }
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
private fun ReportsList(
    reports: List<FullReport>,
    listState: LazyListState,
    goToEditReport: (Int) -> Unit
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
                ReportCard(report = report) {
                    goToEditReport(report.id)
                }
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
