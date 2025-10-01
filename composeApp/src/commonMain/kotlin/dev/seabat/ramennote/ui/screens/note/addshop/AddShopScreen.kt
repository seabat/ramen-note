package dev.seabat.ramennote.ui.screens.note.addshop

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.model.Shop
import dev.seabat.ramennote.domain.util.logd
import dev.seabat.ramennote.ui.component.AppBar
import dev.seabat.ramennote.ui.component.AppAlert
import dev.seabat.ramennote.ui.component.AppTwoButtonAlert
import dev.seabat.ramennote.ui.gallery.createRememberedGalleryLauncher
import dev.seabat.ramennote.ui.permission.PermissionCallback
import dev.seabat.ramennote.ui.permission.PermissionStatus
import dev.seabat.ramennote.ui.permission.PermissionType
import dev.seabat.ramennote.ui.permission.createRememberedPermissionsLauncher
import dev.seabat.ramennote.ui.permission.launchSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import ramennote.composeapp.generated.resources.Res
import ramennote.composeapp.generated.resources.add_evaluation_label
import ramennote.composeapp.generated.resources.add_map_label
import ramennote.composeapp.generated.resources.add_shop_name_label
import ramennote.composeapp.generated.resources.add_shop_no_image
import ramennote.composeapp.generated.resources.add_shop_option1
import ramennote.composeapp.generated.resources.add_shop_photo_label
import ramennote.composeapp.generated.resources.add_shop_register_button
import ramennote.composeapp.generated.resources.add_shop_select_button
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
    var category by remember { mutableStateOf("") }

    val saveState by viewModel.saveState.collectAsState()

    var permissionEnabled by remember { mutableStateOf(false) }
    var shouldLaunchSetting by remember { mutableStateOf(false) }
    var shouldShowPermissionRationalDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    val galleryManager = createRememberedGalleryLauncher {
        coroutineScope.launch {
            val bitmap = withContext(Dispatchers.Default) {
                it?.toImageBitmap()
            }
            imageBitmap = bitmap
        }
    }


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
            nagativeButtonText = "Cancel",
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

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        AppBar(
            title = stringResource(Res.string.add_shop_title),
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // 名前
            ShopInputField(
                label = stringResource(Res.string.add_shop_name_label),
                value = name,
                onValueChange = { name = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Webサイト
            ShopInputField(
                label = stringResource(Res.string.add_web_site_label),
                value = shopUrl,
                onValueChange = { shopUrl = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 地図
            ShopInputField(
                label = stringResource(Res.string.add_map_label),
                value = mapUrl,
                onValueChange = { mapUrl = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 評価
            StarDropdownField(
                label = stringResource(Res.string.add_evaluation_label),
                value = star,
                onValueChange = { star = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 最寄り駅
            ShopInputField(
                label = stringResource(Res.string.add_station_label),
                value = stationName,
                onValueChange = { stationName = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

//            // 系統
//            ShopDropdownField(
//                label = stringResource(Res.string.add_category_label),
//                value = category,
//                onValueChange = { category = it }
//            )

            Spacer(modifier = Modifier.height(16.dp))

            // 写真
            PhotoSelectionField(
                label = stringResource(Res.string.add_shop_photo_label),
                imageBitmap = imageBitmap,
            ) {
                permissionEnabled = true
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 登録ボタン
            val isButtonEnabled = name.isNotBlank() && areaName.isNotBlank()
            
            Button(
                onClick = {
                    val shop = Shop(
                        name = name,
                        area = areaName,
                        shopUrl = shopUrl,
                        mapUrl = mapUrl,
                        star = star,
                        stationName = stationName,
                        category = category
                    )
                    viewModel.saveShop(shop)
                },
                enabled = isButtonEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = stringResource(Res.string.add_shop_register_button),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
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
                            PermissionType.CAMERA -> {  /* カメラ起動の処理 */}
                            PermissionType.GALLERY -> { permissionEnabled() }
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
        logd("ramen-note", "GALLERY Permission: granted")
        showGallery()
    } else {
        logd("ramen-note", "GALLERY Permission: not Granted")
        permissionLauncher.askPermission(PermissionType.GALLERY)
    }
}

@Composable
private fun ShopInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StarDropdownField(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("", "1", "2", "3")
    val displayValue = if (value == 0) "" else value.toString()

    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = displayValue,
                onValueChange = { },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            val intValue = if (option.isEmpty()) 0 else option.toIntOrNull() ?: 0
                            onValueChange(intValue)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ShopDropdownField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("Option 1", "Option 2", "Option 3")

    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box {
            OutlinedTextField(
                value = value.ifEmpty { stringResource(Res.string.add_shop_option1) },
                onValueChange = { },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null
                    )
                }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onValueChange(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PhotoSelectionField(
    label: String,
    imageBitmap: ImageBitmap? = null,
    onClick: () -> Unit
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(Res.string.add_shop_select_button),
                style = MaterialTheme.typography.titleLarge,
                color = Color.Blue,
                modifier = Modifier.clickable { onClick() }
            )
            if (imageBitmap != null) {
                Image(
                    modifier = Modifier
                        .size(120.dp),
                    bitmap = imageBitmap,
                    contentDescription = null
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .border(
                            width = 1.dp,
                            color = Color.Black,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(Res.string.add_shop_no_image),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Black
                    )
                }
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