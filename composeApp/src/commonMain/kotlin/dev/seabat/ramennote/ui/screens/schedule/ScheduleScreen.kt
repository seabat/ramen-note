package dev.seabat.ramennote.ui.screens.schedule

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.seabat.ramennote.domain.extension.isTodayOrFuture
import dev.seabat.ramennote.domain.model.Schedule
import dev.seabat.ramennote.domain.model.Shop
import dev.seabat.ramennote.ui.components.AppBar
import dev.seabat.ramennote.ui.components.alert.AppAlert
import dev.seabat.ramennote.ui.components.alert.AppTwoButtonAlert
import dev.seabat.ramennote.ui.components.button.AddFab
import dev.seabat.ramennote.ui.components.dialog.SelectShopDialog
import dev.seabat.ramennote.ui.theme.RamenNoteTheme
import dev.seabat.ramennote.ui.util.dayOfWeekJp
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import ramennote.composeapp.generated.resources.Res
import ramennote.composeapp.generated.resources.add_schedule_error_past_date_message
import ramennote.composeapp.generated.resources.delete_24px
import ramennote.composeapp.generated.resources.edit_24px
import ramennote.composeapp.generated.resources.ramen_dining_24px
import ramennote.composeapp.generated.resources.schedule_delete_confirm
import ramennote.composeapp.generated.resources.schedule_no_data
import ramennote.composeapp.generated.resources.schedule_picker_label
import ramennote.composeapp.generated.resources.screen_schedule_title
import ramennote.composeapp.generated.resources.select_shop_dialog_create_schedule_button

private sealed interface DialogState {
    object Hidden : DialogState

    object SelectShop : DialogState

    data class AddDatePicker(
        val shop: Shop
    ) : DialogState

    data class EditDatePicker(
        val shopId: Int
    ) : DialogState

    object PastDate : DialogState

    data class DeleteConfirm(
        val shopId: Int
    ) : DialogState
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    goToReport: (shopId: Int, shopName: String, menuName: String, iso8601Date: String) -> Unit = { _, _, _, _ -> },
    goToShop: (shopId: Int, shopName: String) -> Unit = { _, _ -> },
    viewModel: ScheduleViewModelContract = koinViewModel<ScheduleViewModel>()
) {
    val schedules by viewModel.schedules.collectAsStateWithLifecycle()
    var dialogState by remember { mutableStateOf<DialogState>(DialogState.Hidden) }

    LaunchedEffect(Unit) {
        viewModel.loadSchedule()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            AppBar(
                title = stringResource(Res.string.screen_schedule_title)
            )

            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
            ) {
                if (schedules.isNotEmpty()) {
                    ScheduleList(
                        schedules = schedules,
                        onReportClick = { schedule ->
                            goToReport(
                                schedule.shopId,
                                schedule.shopName,
                                schedule.menuName,
                                schedule.scheduledDate?.toString() ?: ""
                            )
                        },
                        onEditClick = { shopId ->
                            dialogState = DialogState.EditDatePicker(shopId)
                        },
                        onDeleteClick = { shopId ->
                            dialogState = DialogState.DeleteConfirm(shopId)
                        },
                        onListItemClick = { schedule ->
                            goToShop(schedule.shopId, schedule.shopName)
                        }
                    )
                } else {
                    Text(
                        text = stringResource(Res.string.schedule_no_data),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }

        // FAB（他のダイアログが開いていない時のみ表示）
        if (dialogState == DialogState.Hidden) {
            AddFab(
                modifier = Modifier.align(Alignment.BottomEnd),
                contentDescription = "予定を追加",
                onAddClick = { dialogState = DialogState.SelectShop }
            )
        }

    }

    ScheduleScreenDialog(
        dialogState = dialogState,
        onDismiss = { dialogState = DialogState.Hidden },
        onSelectShop = { shop -> dialogState = DialogState.AddDatePicker(shop) },
        onShowPastDate = { dialogState = DialogState.PastDate },
        onAddSchedule = { shopId, date -> viewModel.addSchedule(shopId, date) },
        onEditSchedule = { shopId, date -> viewModel.editSchedule(shopId, date) },
        onDeleteSchedule = { shopId -> viewModel.deleteSchedule(shopId) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleScreenDialog(
    dialogState: DialogState,
    onDismiss: () -> Unit,
    onSelectShop: (Shop) -> Unit,
    onShowPastDate: () -> Unit,
    onAddSchedule: (shopId: Int, date: LocalDate) -> Unit,
    onEditSchedule: (shopId: Int, date: LocalDate) -> Unit,
    onDeleteSchedule: (shopId: Int) -> Unit
) {
    val editDatePickerState = rememberDatePickerState()
    val addDatePickerState = rememberDatePickerState()

    when (val state = dialogState) {
        is DialogState.AddDatePicker -> {
            DatePickerDialog(
                onDismissRequest = onDismiss,
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerOnClickHandler(
                                addDatePickerState,
                                showErrorDialog = onShowPastDate,

                                dismissDatePicker = onDismiss,
                                editSchedule = { date -> onAddSchedule(state.shop.id, date) }
                            )
                        }
                    ) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                }
            ) {
                Column {
                    Text(
                        text = stringResource(Res.string.schedule_picker_label),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    DatePicker(state = addDatePickerState)
                }
            }
        }
        is DialogState.EditDatePicker -> {
            DatePickerDialog(
                onDismissRequest = onDismiss,
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerOnClickHandler(
                                editDatePickerState,
                                showErrorDialog = onShowPastDate,

                                dismissDatePicker = onDismiss,
                                editSchedule = { date -> onEditSchedule(state.shopId, date) }
                            )
                        }
                    ) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                }
            ) {
                Column {
                    Text(
                        text = stringResource(Res.string.schedule_picker_label),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    DatePicker(state = editDatePickerState)
                }
            }
        }
        is DialogState.PastDate -> {
            AppAlert(
                message = stringResource(Res.string.add_schedule_error_past_date_message),
                onConfirm = onDismiss
            )
        }
        is DialogState.DeleteConfirm -> {
            AppTwoButtonAlert(
                message = stringResource(Res.string.schedule_delete_confirm),
                onConfirm = {
                    onDeleteSchedule(state.shopId)
                    onDismiss()
                },
                onNegative = onDismiss
            )
        }
        is DialogState.SelectShop -> {
            SelectShopDialog(
                onDismiss = onDismiss,
                confirmButtonLabel = stringResource(Res.string.select_shop_dialog_create_schedule_button),
                onConfirmClick = { shop -> onSelectShop(shop) }
            )
        }
        is DialogState.Hidden -> {
            // 何も表示しない
        }
    }
}

@Composable
private fun ScheduleList(
    schedules: List<Schedule>,
    onReportClick: (schedule: Schedule) -> Unit,
    onEditClick: (shopId: Int) -> Unit,
    onDeleteClick: (shopId: Int) -> Unit,
    onListItemClick: (schedule: Schedule) -> Unit
) {
    // グルーピング: 年月ごと (YYYY-MM)
    val grouped: Map<String, List<Schedule>> =
        schedules
            .groupBy { schedule ->
                val date = schedule.scheduledDate
                if (date != null) "${date.year}-${date.monthNumber.toString().padStart(2, '0')}" else ""
            }.filterKeys { it.isNotEmpty() }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            HorizontalDivider(
                Modifier,
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )
        }

        grouped.forEach { (yearMonth, monthSchedules) ->
            item {
                Text(
                    text = yearMonth,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            item {
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
            }

            items(monthSchedules) { schedule ->
                ScheduleRow(
                    schedule = schedule,
                    onReportClick = {
                        onReportClick(schedule)
                    },
                    onEditClick = {
                        onEditClick(schedule.shopId)
                    },
                    onDeleteClick = {
                        onDeleteClick(schedule.shopId)
                    },
                    onListItemClick = {
                        onListItemClick(schedule)
                    }
                )
            }
        }
    }
}

@Composable
private fun ScheduleRow(
    schedule: Schedule,
    onReportClick: () -> Unit = {},
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onListItemClick: () -> Unit = {}
) {
    val date: LocalDate? = schedule.scheduledDate
    val dayText =
        if (date != null) {
            "${date.dayOfMonth.toString().padStart(2, '0')} (${dayOfWeekJp(date)})"
        } else {
            ""
        }

    Column {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable {
                        onListItemClick()
                    },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(text = dayText, style = MaterialTheme.typography.titleMedium)
                Text(text = schedule.shopName, style = MaterialTheme.typography.titleMedium)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                if (!schedule.isReported) {
                    Icon(
                        painter = painterResource(Res.drawable.ramen_dining_24px),
                        contentDescription = "食レポ",
                        modifier =
                            Modifier
                                .size(24.dp)
                                .clickable {
                                    onReportClick()
                                },
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(
                    painter = painterResource(Res.drawable.edit_24px),
                    contentDescription = "編集",
                    modifier =
                        Modifier
                            .size(24.dp)
                            .clickable {
                                onEditClick()
                            },
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(
                    painter = painterResource(Res.drawable.delete_24px),
                    contentDescription = "削除",
                    modifier =
                        Modifier
                            .size(24.dp)
                            .clickable {
                                onDeleteClick()
                            },
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    }
}

/**
 * DatePickerDialog の OK ボタン押下時の処理。
 *
 * 選択された日付が今日以降であれば [editSchedule] を呼び出して [dismissDatePicker] でダイアログを閉じる。
 * 過去の日付が選択された場合は [showErrorDialog] を呼び出し、ダイアログは閉じない。
 * 日付が未選択の場合は [dismissDatePicker] のみ呼び出す。
 *
 * @param datePickerState DatePicker の状態。選択日付ミリ秒を取得するために使用する
 * @param showErrorDialog 過去日付が選択されたときに呼び出すコールバック
 * @param editSchedule 有効な日付が選択されたときに呼び出すコールバック。選択された [LocalDate] を受け取る
 * @param dismissDatePicker DatePickerDialog を閉じるコールバック。過去日付エラー時は呼ばれない
 */
@OptIn(ExperimentalMaterial3Api::class)
private fun datePickerOnClickHandler(
    datePickerState: DatePickerState,
    showErrorDialog: () -> Unit,
    editSchedule: (LocalDate) -> Unit = { _ -> },
    dismissDatePicker: () -> Unit
) {
    val millis = datePickerState.selectedDateMillis
    if (millis != null) {
        val date =
            Instant
                .fromEpochMilliseconds(millis)
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .date

        // 今日を含めた未来日かどうかをチェック
        if (!date.isTodayOrFuture()) {
            // エラーダイアログを表示するため dismissDatePicker は呼ばない
            showErrorDialog()
        } else {
            editSchedule(date)
            dismissDatePicker()
        }
    } else {
        dismissDatePicker()
    }
}

@Preview
@Composable
fun ScheduleScreenPreview() {
    RamenNoteTheme {
        ScheduleScreen(
            goToReport = { _, _, _, _ -> },
            goToShop = { _, _ -> },
            viewModel = MockScheduleViewModel()
        )
    }
}
