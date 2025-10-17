package dev.seabat.ramennote.ui.screens.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.seabat.ramennote.domain.model.FullReport
import dev.seabat.ramennote.ui.components.AppBar
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.jetbrains.compose.resources.stringResource
import ramennote.composeapp.generated.resources.Res
import ramennote.composeapp.generated.resources.screen_history_title
import dev.seabat.ramennote.ui.theme.RamenNoteTheme
import dev.seabat.ramennote.ui.util.dayOfWeekJp
import kotlinx.datetime.LocalDate
import ramennote.composeapp.generated.resources.history_no_data

@Composable
fun HistoryScreen(
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
            ReportsList(reports = reports)
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
    reports: List<FullReport>
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
                ReportRow(report = report)
            }
        }
    }
}

@Composable
private fun ReportRow(
    report: FullReport
) {
    val date: LocalDate? = report.date
    val dayText = if (date != null) {
        "${date.dayOfMonth.toString().padStart(2, '0')} (${dayOfWeekJp(date)})"
    } else {
        ""
    }

    Card(
        modifier = Modifier.fillMaxWidth().height(150.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = report.imageBytes,
                contentDescription = null,
                modifier = Modifier.size(100.dp), //TODO: adjust size
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1.0f)
            ) {
                Text(
                    text = report.shopName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = report.menuName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = dayText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (report.impression.isNotBlank()) {
                    Text(
                        text = report.impression,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
