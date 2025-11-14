package dev.seabat.ramennote.ui.screens.note.addshop

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.model.Shop
import dev.seabat.ramennote.domain.util.logd
import dev.seabat.ramennote.ui.components.AppAlert
import dev.seabat.ramennote.ui.components.AppBar
import dev.seabat.ramennote.ui.components.AppTwoButtonAlert
import dev.seabat.ramennote.ui.components.MaxWidthButton
import dev.seabat.ramennote.ui.components.StarIcon
import dev.seabat.ramennote.ui.gallery.SharedImage
import dev.seabat.ramennote.ui.gallery.createRememberedGalleryLauncher
import dev.seabat.ramennote.ui.permission.PermissionCallback
import dev.seabat.ramennote.ui.permission.PermissionStatus
import dev.seabat.ramennote.ui.permission.PermissionType
import dev.seabat.ramennote.ui.permission.createRememberedPermissionsLauncher
import dev.seabat.ramennote.ui.permission.launchSettings
import dev.seabat.ramennote.ui.screens.note.shop.RamenField
import dev.seabat.ramennote.ui.screens.note.shop.ShopDropdownField
import dev.seabat.ramennote.ui.screens.note.shop.ShopInputField
import dev.seabat.ramennote.ui.screens.note.shop.ShopMultilineInputField
import kotlinx.datetime.Clock
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import ramennote.composeapp.generated.resources.Res
import ramennote.composeapp.generated.resources.add_category_label
import ramennote.composeapp.generated.resources.add_evaluation_label
import ramennote.composeapp.generated.resources.add_map_label
import ramennote.composeapp.generated.resources.add_note_label
import ramennote.composeapp.generated.resources.add_shop_name_label
import ramennote.composeapp.generated.resources.add_shop_register_button
import ramennote.composeapp.generated.resources.add_shop_title
import ramennote.composeapp.generated.resources.add_station_label
import ramennote.composeapp.generated.resources.add_web_site_label

@Composable
fun AddShopScreen(
    areaName: String,
    onBackClick: () -> Unit,
    onCompleted: () -> Unit,
    viewModel: AddShopViewModelContract = koinViewModel<AddShopViewModel>()
) {
    var name by remember { mutableStateOf("") }
    var shopUrl by remember { mutableStateOf("") }
    var mapUrl by remember { mutableStateOf("") }
    var star by remember { mutableStateOf(1) }
    var stationName by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    var category by remember { mutableStateOf("") }
    var menuName by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    val saveState by viewModel.saveState.collectAsState()

    var permissionEnabled by remember { mutableStateOf(false) }
    var shouldLaunchSetting by remember { mutableStateOf(false) }
    var shouldShowPermissionRationalDialog by remember { mutableStateOf(false) }
    var sharedImage by remember { mutableStateOf<SharedImage?>(null) }
    val galleryManager = createRememberedGalleryLauncher { sharedImage = it }

    if (permissionEnabled) {
        Permission(
            permissionEnabled = {
                permissionEnabled = true
            },
            showPermissionRationalDialog = {
                shouldShowPermissionRationalDialog = true
            },
            showGallery = {
                galleryManager.launch()
            }
        )
        permissionEnabled = false
    }

    if (shouldShowPermissionRationalDialog) {
        AppTwoButtonAlert(
            message = "写真を選択するには、ストレージのアクセス許可が必要です。設定から許可してください。",
            confirmButtonText = "Settings",
            negativeButtonText = "Cancel",
            onConfirm = {
                shouldShowPermissionRationalDialog = false
                shouldLaunchSetting = true
            },
            onNegative = {
                shouldShowPermissionRationalDialog = false
            }
        )
    }

    if (shouldLaunchSetting) {
        launchSettings()
        shouldLaunchSetting = false
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            AppBar(
                title = stringResource(Res.string.add_shop_title),
                onBackClick = onBackClick
            )

            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    val released = tryAwaitRelease()
                                    if (released) {
                                        focusManager.clearFocus()
                                    }
                                }
                            )
                        }
            ) {
                // 名前
                ShopInputField(
                    label = stringResource(Res.string.add_shop_name_label),
                    value = name,
                    onValueChange = { name = it }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Webサイト
                ShopInputField(
                    label = stringResource(Res.string.add_web_site_label),
                    value = shopUrl,
                    onValueChange = { shopUrl = it }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 地図
                ShopInputField(
                    label = stringResource(Res.string.add_map_label),
                    value = mapUrl,
                    onValueChange = { mapUrl = it }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 評価
                StarRating(
                    star = star,
                    onValueChange = { star = it }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 最寄り駅
                ShopInputField(
                    label = stringResource(Res.string.add_station_label),
                    value = stationName,
                    onValueChange = { stationName = it }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 系統
                ShopDropdownField(
                    label = stringResource(Res.string.add_category_label),
                    value = category,
                    onValueChange = { category = "$it" }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // ノート
                ShopMultilineInputField(
                    label = stringResource(Res.string.add_note_label),
                    value = note,
                    onValueChange = { note = it }
                )

                Spacer(modifier = Modifier.height(20.dp))

                // メニュー情報
                RamenField(
                    menuName = menuName,
                    sharedImage = sharedImage,
                    enablePermission = { permissionEnabled = true },
                    onMenuValueChange = { menuName = it }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // 登録ボタン
                val isButtonEnabled = name.isNotBlank() && areaName.isNotBlank()

                MaxWidthButton(
                    text = stringResource(Res.string.add_shop_register_button),
                    enabled = isButtonEnabled
                ) {
                    val shop =
                        Shop(
                            name = name,
                            area = areaName,
                            shopUrl = shopUrl,
                            mapUrl = mapUrl,
                            star = star,
                            stationName = stationName,
                            category = category,
                            menuName1 = menuName,
                            photoName1 = createPhotoName(1),
                            note = note
                        )

                    // sharedImageがnullの場合はnoimage.svgをByteArrayに変換してSharedImageを作成
                    val finalSharedImage = sharedImage ?: SharedImage(viewModel.createNoImage())
                    viewModel.saveShop(shop, finalSharedImage)
                }
            }
        }
        // 保存状態の処理
        when (saveState) {
            is RunStatus.Success -> {
                onCompleted()
            }
            is RunStatus.Error -> {
                AppAlert(
                    message = saveState.message ?: "不明なエラーが発生しました",
                    onConfirm = { /* エラー処理 */ }
                )
            }
            else -> { /* その他の状態は何もしない */ }
        }
    }
}

private fun createPhotoName(number: Int): String {
    val now =
        Clock.System.now().toLocalDateTime(
            kotlinx.datetime.TimeZone.currentSystemDefault()
        )
    val currentTime = "${now.year}${now.monthNumber.toString()
        .padStart(2, '0')}${now.dayOfMonth.toString()
        .padStart(2, '0')}T${now.hour.toString()
        .padStart(2, '0')}${now.minute.toString()
        .padStart(2, '0')}${now.second.toString()
        .padStart(2, '0')}"
    return currentTime + "_$number"
}

@Composable
private fun Permission(
    permissionEnabled: () -> Unit,
    showPermissionRationalDialog: () -> Unit,
    showGallery: () -> Unit
) {
    val currentCallback by rememberUpdatedState(
        object : PermissionCallback {
            override fun onPermissionStatus(
                permissionType: PermissionType,
                status: PermissionStatus
            ) {
                when (status) {
                    PermissionStatus.GRANTED -> {
                        when (permissionType) {
                            PermissionType.CAMERA -> { /* カメラ起動の処理 */ }
                            PermissionType.GALLERY -> {
                                permissionEnabled()
                            }
                        }
                    }
                    else -> {
                        showPermissionRationalDialog()
                    }
                }
            }
        }
    )
    val permissionLauncher = createRememberedPermissionsLauncher(currentCallback)
    val isGranted = permissionLauncher.isPermissionGranted(PermissionType.GALLERY)
    if (isGranted) {
        logd(message = "GALLERY Permission(granted)")
        showGallery()
    } else {
        logd(message = "GALLERY Permission(not Granted)")
        permissionLauncher.askPermission(PermissionType.GALLERY)
    }
}

@Composable
private fun StarRating(
    star: Int,
    onValueChange: (Int) -> Unit
) {
    Column {
        Text(
            text = stringResource(Res.string.add_evaluation_label),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row {
            repeat(3) { index ->
                StarIcon(
                    onOff = index < star,
                    onClick = { onValueChange(index + 1) }
                )
            }
        }
    }
}

@Preview
@Composable
fun AddShopScreenPreview() {
    AddShopScreen(
        areaName = "Tokyo",
        onBackClick = {},
        onCompleted = {},
        viewModel = MockAddShopViewModel()
    )
}
