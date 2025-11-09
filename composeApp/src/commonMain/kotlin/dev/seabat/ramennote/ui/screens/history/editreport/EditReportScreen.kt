package dev.seabat.ramennote.ui.screens.history.editreport

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.ui.components.AppAlert
import dev.seabat.ramennote.ui.components.AppBar
import dev.seabat.ramennote.ui.components.AppProgressBar
import dev.seabat.ramennote.ui.components.AppTwoButtonAlert
import dev.seabat.ramennote.ui.components.MaxWidthButton
import dev.seabat.ramennote.ui.components.PhotoSelectionHandler
import dev.seabat.ramennote.ui.gallery.SharedImage
import dev.seabat.ramennote.ui.screens.note.shop.RamenField
import dev.seabat.ramennote.ui.screens.note.shop.ShopDetailItem
import dev.seabat.ramennote.ui.screens.note.shop.ShopInputField
import dev.seabat.ramennote.ui.util.createFormattedDateString
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import ramennote.composeapp.generated.resources.Res
import ramennote.composeapp.generated.resources.editreport_delete_button
import ramennote.composeapp.generated.resources.editreport_edit_button
import ramennote.composeapp.generated.resources.report_delete_confirm
import ramennote.composeapp.generated.resources.report_header
import ramennote.composeapp.generated.resources.report_impressions
import ramennote.composeapp.generated.resources.report_select_date
import ramennote.composeapp.generated.resources.report_shop_name

private sealed interface ErrorDialogType {
    object Hidden : ErrorDialogType

    data class DeleteConfirm(
        val reportId: Int
    ) : ErrorDialogType
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditReportScreen(
    reportId: Int,
    onBackClick: () -> Unit,
    viewModel: EditReportViewModelContract = koinViewModel<EditReportViewModel>()
) {
    val fullReport by viewModel.fullReport.collectAsState()

    var menuName by remember(fullReport.menuName) { mutableStateOf(fullReport.menuName) }
    var image by remember(fullReport.imageBytes) {
        mutableStateOf(
            fullReport.imageBytes?.let { imageBytes ->
                SharedImage(imageBytes)
            } ?: SharedImage()
        )
    }
    var reportedDate by remember(fullReport.date) { mutableStateOf(fullReport.date) }
    var impression by remember(fullReport.impression) { mutableStateOf(fullReport.impression) }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    val editedStatus by viewModel.editedStatus.collectAsState()
    val deletedStatus by viewModel.deletedStatus.collectAsState()

    var permissionEnabled by remember { mutableStateOf(false) }
    var errorDialogType by remember { mutableStateOf<ErrorDialogType>(ErrorDialogType.Hidden) }

    PhotoSelectionHandler(
        onImageSelected = { image = it },
        permissionEnabled = permissionEnabled,
        onPermissionEnabledChange = { permissionEnabled = it }
    )

    LaunchedEffect(Unit) {
        viewModel.loadReport(reportId)
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            AppBar(
                title = stringResource(Res.string.report_header),
                onBackClick = onBackClick
            )

            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
            ) {
                // 店名
                ShopDetailItem(
                    label = stringResource(Res.string.report_shop_name),
                    value = fullReport.shopName
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 日付選択
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(Res.string.report_select_date),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Blue,
                        modifier = Modifier.clickable { showDatePicker = true }
                    )
                    Text(text = createFormattedDateString(reportedDate))
                }

                Spacer(modifier = Modifier.height(24.dp))

                RamenField(
                    menuName = menuName,
                    sharedImage = image,
                    enablePermission = { permissionEnabled = true },
                    onMenuValueChange = { menuName = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 感想
                ShopInputField(
                    label = stringResource(Res.string.report_impressions),
                    value = impression,
                    singleLine = false,
                    onValueChange = { impression = it }
                )

                Spacer(modifier = Modifier.weight(1f))

                BottomButtons(
                    onEditButtonClick = {
                        viewModel.editReport(
                            menuName = menuName,
                            reportedDate = reportedDate,
                            impression = impression,
                            shopId = fullReport.shopId,
                            image = image
                        )
                    },
                    onDeleteButtonClick = {
                        errorDialogType = ErrorDialogType.DeleteConfirm(fullReport.id)
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
                                val date =
                                    Instant
                                        .fromEpochMilliseconds(millis)
                                        .toLocalDateTime(TimeZone.currentSystemDefault())
                                        .date
                                reportedDate = date
                            }
                            showDatePicker = false
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
        EditedStatus(
            status = editedStatus,
            onCompleted = {
                viewModel.setReportedStatusToIdle()
                onBackClick()
            },
            onErrorClosed = {
                viewModel.setReportedStatusToIdle()
            }
        )
        DeletedStatus(
            status = deletedStatus,
            onCompleted = {
                onBackClick()
                viewModel.setDeletedStatusToIdle()
            },
            onErrorClosed = {
                viewModel.setDeletedStatusToIdle()
            }
        )

        // エラーダイアログ
        when (val shouldShow = errorDialogType) {
            is ErrorDialogType.DeleteConfirm -> {
                AppTwoButtonAlert(
                    message = stringResource(Res.string.report_delete_confirm),
                    onConfirm = {
                        viewModel.deleteReport(shouldShow.reportId)
                        errorDialogType = ErrorDialogType.Hidden
                    },
                    onNegative = {
                        errorDialogType = ErrorDialogType.Hidden
                    }
                )
            }
            is ErrorDialogType.Hidden -> {
                // 何も表示しない
            }
        }
    }
}

@Composable
fun EditedStatus(
    status: RunStatus<Int>,
    onCompleted: () -> Unit,
    onErrorClosed: () -> Unit
) {
    when (status) {
        is RunStatus.Success -> {
            onCompleted()
        }
        is RunStatus.Error -> {
            AppAlert(
                message = "${status.message}",
                onConfirm = { onErrorClosed() }
            )
        }
        is RunStatus.Loading -> {
            AppProgressBar()
        }
        is RunStatus.Idle -> { /* Do nothing */ }
    }
}

@Composable
fun DeletedStatus(
    status: RunStatus<String>,
    onCompleted: () -> Unit,
    onErrorClosed: () -> Unit
) {
    when (status) {
        is RunStatus.Success -> {
            onCompleted()
        }
        is RunStatus.Error -> {
            AppAlert(
                message = "${status.message}",
                onConfirm = { onErrorClosed() }
            )
        }
        is RunStatus.Loading -> {
            AppProgressBar()
        }
        is RunStatus.Idle -> { /* Do nothing */ }
    }
}

@Composable
fun BottomButtons(
    onEditButtonClick: () -> Unit,
    onDeleteButtonClick: () -> Unit
) {
    Column {
        MaxWidthButton(text = stringResource(Res.string.editreport_edit_button)) {
            onEditButtonClick()
        }
        MaxWidthButton(text = stringResource(Res.string.editreport_delete_button)) {
            onDeleteButtonClick()
        }
    }
}
