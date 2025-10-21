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
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.seabat.ramennote.domain.model.FullReport
import dev.seabat.ramennote.ui.components.AppBar
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.jetbrains.compose.resources.stringResource
import ramennote.composeapp.generated.resources.Res
import ramennote.composeapp.generated.resources.screen_history_title
import dev.seabat.ramennote.ui.theme.RamenNoteTheme
import ramennote.composeapp.generated.resources.history_no_data
import kotlinx.coroutines.delay

@Composable
fun HistoryScreen(
    goToEditReport: (reportId: Int) -> Unit = {},
    viewModel: HistoryViewModelContract = koinViewModel<HistoryViewModel>()
) {
    val reports by viewModel.reports.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadReports() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        AppBar(title = stringResource(Res.string.screen_history_title),)
        if (reports.isNotEmpty()) {
            Box {
                // レポート一覧
                ReportsList(reports = reports, goToEditReport = goToEditReport)

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
    goToEditReport: (Int) -> Unit
) {
    // グルーピング: 年月ごと (YYYY-MM)
    val grouped: Map<String, List<FullReport>> = reports
        .sortedByDescending { it.date }
        .groupBy { report ->
            val date = report.date
            "${date.year}-${date.monthNumber.toString().padStart(2, '0')}"
        }.filterKeys { it.isNotEmpty() }

    LazyColumn(
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