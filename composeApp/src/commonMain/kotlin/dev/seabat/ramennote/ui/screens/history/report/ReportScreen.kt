package dev.seabat.ramennote.ui.screens.history.report

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.seabat.ramennote.domain.model.Shop
import dev.seabat.ramennote.ui.components.AppBar
import dev.seabat.ramennote.ui.components.MaxWidthButton
import dev.seabat.ramennote.ui.components.PhotoSelectionHandler
import dev.seabat.ramennote.ui.gallery.SharedImage
import dev.seabat.ramennote.ui.screens.note.shop.RamenField
import dev.seabat.ramennote.ui.screens.note.shop.ShopInputField
import dev.seabat.ramennote.ui.util.createFormattedDateString
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import ramennote.composeapp.generated.resources.Res
import ramennote.composeapp.generated.resources.report_header
import ramennote.composeapp.generated.resources.report_impressions
import ramennote.composeapp.generated.resources.report_no_set
import ramennote.composeapp.generated.resources.report_run
import ramennote.composeapp.generated.resources.report_select_date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    shop: Shop,
    onBackClick: () -> Unit,
    goToHistory: () -> Unit,
    viewModel: ReportViewModelContract = koinViewModel<ReportViewModel>()
) {
    var menuName by remember { mutableStateOf(shop.menuName1) }
    var image by remember { mutableStateOf(SharedImage()) }
    var reportedDate by remember {
        mutableStateOf(shop.scheduledDate ?:Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date)
    }
    var impression by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Text(
                        text = stringResource(Res.string.report_select_date),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Blue,
                        modifier = Modifier.clickable { showDatePicker = true }
                    )
                    Text(
                        text = reportedDate?.let {
                            createFormattedDateString(it)
                        } ?: stringResource(Res.string.report_no_set)
                    )
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
                    onValueChange = { impression = it },
                )

                Spacer(modifier = Modifier.weight(1f))

                MaxWidthButton(
                    text = stringResource(Res.string.report_run)
                ) {
                    viewModel.report(
                        menuName =menuName,
                        reportedDate = reportedDate,
                        impression = impression,
                        shop = shop,
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
    }
}