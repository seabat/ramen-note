package dev.seabat.ramennote.ui.screens.history.addreport

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.skydoves.navgraph.annotations.NavDestination
import com.github.skydoves.navgraph.annotations.NavEdge
import com.github.skydoves.navgraph.annotations.NavPreview
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.util.createTodayLocalDate
import dev.seabat.ramennote.ui.components.AppBar
import dev.seabat.ramennote.ui.components.AppProgressBar
import dev.seabat.ramennote.ui.components.PhotoSelectionHandler
import dev.seabat.ramennote.ui.components.alert.AppAlert
import dev.seabat.ramennote.ui.components.button.MaxWidthButton
import dev.seabat.ramennote.ui.gallery.SharedImage
import dev.seabat.ramennote.ui.navigation.Screen
import dev.seabat.ramennote.ui.screens.componens.DateSelectItem
import dev.seabat.ramennote.ui.screens.componens.RamenField
import dev.seabat.ramennote.ui.screens.componens.ReportStarRatingItem
import dev.seabat.ramennote.ui.screens.componens.ShopDetailItem
import dev.seabat.ramennote.ui.screens.componens.ShopInputField
import dev.seabat.ramennote.ui.share.createPostText
import dev.seabat.ramennote.ui.share.createRememberedXShareLauncher
import dev.seabat.ramennote.ui.theme.RamenNoteTheme
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import ramennote.sharedui.generated.resources.Res
import ramennote.sharedui.generated.resources.report_header
import ramennote.sharedui.generated.resources.report_impressions
import ramennote.sharedui.generated.resources.report_post_x
import ramennote.sharedui.generated.resources.report_run
import ramennote.sharedui.generated.resources.report_shop_name
import kotlin.time.Instant

@NavDestination(route = Screen.Report::class)
@NavEdge(to = Screen.History::class, label = "履歴へ")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReportScreen(
    shopId: Int,
    shopName: String,
    menuName: String,
    scheduledDate: LocalDate? = null,
    onBackClick: () -> Unit,
    goToHistory: () -> Unit,
    viewModel: AddReportViewModelContract = koinViewModel<AddReportViewModel>()
) {
    var menuName by remember { mutableStateOf(menuName) }
    var image by remember { mutableStateOf(SharedImage()) }
    var reportedDate by remember {
        mutableStateOf(scheduledDate ?: createTodayLocalDate())
    }
    var star by remember { mutableStateOf(1) }
    var impression by remember { mutableStateOf("") }
    var postToX by remember { mutableStateOf(false) }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    val reportedStatus by viewModel.reportedStatus.collectAsState()

    var permissionEnabled by remember { mutableStateOf(false) }

    val xShareLauncher = createRememberedXShareLauncher()

    PhotoSelectionHandler(
        onImageSelected = { image = it },
        permissionEnabled = permissionEnabled,
        onPermissionEnabledChange = { permissionEnabled = it }
    )

    Box(
        modifier =
            Modifier
                .fillMaxSize()
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
                        .padding(horizontal = 16.dp)
            ) {
                // 店名
                ShopDetailItem(
                    label = stringResource(Res.string.report_shop_name),
                    value = shopName
                )

                // 日付選択
                DateSelectItem(reportedDate) {
                    showDatePicker = true
                }

                // 評価
                ReportStarRatingItem(
                    star = star,
                    onValueChange = { star = it }
                )

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
                    onValueChange = { impression = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                PostX(
                    checked = postToX,
                    onCheckedChange = { postToX = it }
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
                        image = image,
                        star = star
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
        ReportStatus(
            status = reportedStatus,
            onCompleted = {
                viewModel.setReportedStatusToIdle()
                if (postToX) {
                    viewModel.shareToX(createPostText(shopName, menuName, impression), image, xShareLauncher)
                }
                onBackClick()
                // FIXME: History に遷移した後に Schedule タブをタップすると Report が表示されてしまう
//                goToHistory()
            },
            onErrorClosed = {
                viewModel.setReportedStatusToIdle()
            }
        )
    }
}

@Composable
fun PostX(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
        Text(
            text = stringResource(Res.string.report_post_x),
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
fun ReportStatus(
    status: RunStatus<Int>,
    onCompleted: () -> Unit,
    onErrorClosed: () -> Unit
) {
    // RunStatus.Successの場合にXへの投稿処理を実行
    LaunchedEffect(status) {
    }

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

@NavPreview(route = Screen.Report::class, primary = true)
@Preview
@Composable
fun ReportScreenPreview() {
    RamenNoteTheme {
        AddReportScreen(
            shopId = 1,
            shopName = "〇〇家",
            menuName = "味玉らーめん",
            scheduledDate = null,
            onBackClick = { },
            goToHistory = { },
            viewModel = MockAddReportViewModel()
        )
    }
}
