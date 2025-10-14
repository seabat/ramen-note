package dev.seabat.ramennote.ui.screens.schedule

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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
import dev.seabat.ramennote.domain.model.Shop
import dev.seabat.ramennote.ui.components.AppBar
import dev.seabat.ramennote.ui.theme.RamenNoteTheme
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import ramennote.composeapp.generated.resources.Res
import ramennote.composeapp.generated.resources.delete_24px
import ramennote.composeapp.generated.resources.edit_24px
import ramennote.composeapp.generated.resources.screen_schedule_title

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModelContract = koinViewModel<ScheduleViewModel>()
) {
    val shops by viewModel.scheduledShops.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }
    var clickedShopId by remember { mutableStateOf(0) }
    val datePickerState = rememberDatePickerState()

    LaunchedEffect(Unit) {
        viewModel.loadSchedule()
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        AppBar(
            title = stringResource(Res.string.screen_schedule_title)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            ScheduleList(
                shops = shops,
                onEditClick = { shopId ->
                    showDatePicker = true
                    clickedShopId = shopId
                },
                onDeleteClick = { shopId ->
                    viewModel.deleteSchedule(shopId)
                }
            )
        }
    }
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val millis = datePickerState.selectedDateMillis
                        if (millis != null) {
                            val date = Instant.fromEpochMilliseconds(millis)
                                .toLocalDateTime(TimeZone.currentSystemDefault()).date
                            viewModel.editSchedule(clickedShopId, date)
                        }
                        showDatePicker = false
                        clickedShopId = 0
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
private fun ScheduleList(
    shops: List<Shop>,
    onEditClick: (shopId: Int) -> Unit,
    onDeleteClick: (shopId: Int) -> Unit
) {
    // グルーピング: 年月ごと (YYYY-MM)
    val grouped: Map<String, List<Shop>> = shops.groupBy { shop ->
        val date = shop.scheduledDate
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

        grouped.forEach { (yearMonth, monthShops) ->
            item {
                Text(
                    text = yearMonth,
                    style = MaterialTheme.typography.titleMedium,
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

            items(monthShops) { shop ->
                ScheduleRow(
                    shop = shop,
                    onEditClick = {
                        onEditClick(shop.id)
                    },
                    onDeleteClick = {
                        onDeleteClick(shop.id)
                    }
                )
            }
        }
    }
}

@Composable
private fun ScheduleRow(
    shop: Shop,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val date: LocalDate? = shop.scheduledDate
    val dayText = if (date != null) {
        "${date.dayOfMonth.toString().padStart(2, '0')} (${dayOfWeekJp(date)})"
    } else {
        ""
    }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(text = dayText, style = MaterialTheme.typography.titleMedium)
                Text(text = shop.name, style = MaterialTheme.typography.titleMedium)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(Res.drawable.edit_24px),
                    contentDescription = "編集",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            onEditClick()
                        },
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(
                    painter = painterResource(Res.drawable.delete_24px),
                    contentDescription = "削除",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            onDeleteClick()
                        },
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Divider(
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
            thickness = 1.dp
        )
    }
}

private fun dayOfWeekJp(date: LocalDate): String {
    return when (date.dayOfWeek.isoDayNumber) {
        1 -> "月"
        2 -> "火"
        3 -> "水"
        4 -> "木"
        5 -> "金"
        6 -> "土"
        7 -> "日"
        else -> ""
    }
}

@Preview
@Composable
fun ScheduleScreenPreview() {
    RamenNoteTheme {
        ScheduleScreen(viewModel = MockScheduleViewModel())
    }
}
