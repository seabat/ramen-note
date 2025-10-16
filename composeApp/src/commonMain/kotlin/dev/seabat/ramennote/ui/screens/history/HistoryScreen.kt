package dev.seabat.ramennote.ui.screens.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.jetbrains.compose.resources.stringResource
import ramennote.composeapp.generated.resources.Res
import ramennote.composeapp.generated.resources.screen_history_title
import dev.seabat.ramennote.ui.theme.RamenNoteTheme

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
        Text(
            text = stringResource(Res.string.screen_history_title),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        ReportsList(reports = reports)
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
    reports: List<dev.seabat.ramennote.domain.model.Report>
) {
    // グルーピング: 年月ごと (YYYY-MM)
    val grouped: Map<String, List<dev.seabat.ramennote.domain.model.Report>> = reports
        .sortedBy { it.date }
        .groupBy { report ->
            val date = report.date
            if (date != null) "${date.year}-${date.monthNumber.toString().padStart(2, '0')}" else ""
        }.filterKeys { it.isNotEmpty() }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Divider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                thickness = 1.dp
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

            item {
                Divider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    thickness = 1.dp
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
    report: dev.seabat.ramennote.domain.model.Report
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = report.menuName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = report.date?.toString() ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (report.impression.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = report.impression,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
