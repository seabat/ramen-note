package dev.seabat.ramennote.ui.screens.history.report

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.util.createTodayLocalDate
import dev.seabat.ramennote.ui.components.AppAlert
import dev.seabat.ramennote.ui.components.AppBar
import dev.seabat.ramennote.ui.components.AppProgressBar
import dev.seabat.ramennote.ui.components.MaxWidthButton
import dev.seabat.ramennote.ui.components.PhotoSelectionHandler
import dev.seabat.ramennote.ui.gallery.SharedImage
import dev.seabat.ramennote.ui.screens.note.shop.DateSelectItem
import dev.seabat.ramennote.ui.screens.note.shop.RamenField
import dev.seabat.ramennote.ui.screens.note.shop.ShopDetailItem
import dev.seabat.ramennote.ui.screens.note.shop.ShopInputField
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import ramennote.composeapp.generated.resources.Res
import ramennote.composeapp.generated.resources.report_header
import ramennote.composeapp.generated.resources.report_impressions
import ramennote.composeapp.generated.resources.report_run
import ramennote.composeapp.generated.resources.report_shop_name

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    shopId: Int,
    shopName: String,
    menuName: String,
    scheduledDate: LocalDate? = null,
    onBackClick: () -> Unit,
    goToHistory: () -> Unit,
    viewModel: ReportViewModelContract = koinViewModel<ReportViewModel>()
) {
    var menuName by remember { mutableStateOf(menuName) }
    var image by remember { mutableStateOf(SharedImage()) }
    var reportedDate by remember {
        mutableStateOf(scheduledDate ?:createTodayLocalDate())
    }
    var impression by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    val reportedStatus by viewModel.reportedStatus.collectAsState()

    var permissionEnabled by remember { mutableStateOf(false) }

    PhotoSelectionHandler(
        onImageSelected = { image = it },
        permissionEnabled = permissionEnabled,
        onPermissionEnabledChange = { permissionEnabled = it }
    )

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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                // 店名
                ShopDetailItem(
                    label = stringResource(Res.string.report_shop_name),
                    value = shopName,
                )

                // 日付選択
                DateSelectItem(reportedDate) {
                    showDatePicker = true
                }

                Spacer(modifier = Modifier.height(16.dp))

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
                    onValueChange = { impression = it },
                )

                Spacer(modifier = Modifier.weight(1f))

                MaxWidthButton(
                    text = stringResource(Res.string.report_run)
                ) {
                    viewModel.report(
                        menuName = menuName,
                        reportedDate = reportedDate,
                        impression = impression,
                        shopId = shopId,
                        image = image
                    )
                }
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
        ReportStatus(
            status = reportedStatus,
            onCompleted ={
                viewModel.setReportedStatusToIdle()
                onBackClick()
                //FIXME: History に遷移した後に Schedule タブをタップすると Report が表示されてしまう
//                goToHistory()
            },
            onErrorClosed = {
                viewModel.setReportedStatusToIdle()
            }
        )
    }
}

@Composable
fun ReportStatus(
    status: RunStatus<Int>,
    onCompleted: () -> Unit,
    onErrorClosed: () -> Unit
) {
    when (status) {
        is RunStatus.Success -> { onCompleted() }
        is RunStatus.Error -> {
            AppAlert(
                message = "${status.message}",
                onConfirm = { onErrorClosed() }
            )
        }
        is RunStatus.Loading -> { AppProgressBar() }
        is RunStatus.Idle -> { /* Do nothing */}
    }
}