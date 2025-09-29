package dev.seabat.ramennote.ui.screens.note.addshop

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.model.Shop
import dev.seabat.ramennote.ui.component.AppBar
import dev.seabat.ramennote.ui.component.AppAlert
import dev.seabat.ramennote.ui.permission.PermissionCallback
import dev.seabat.ramennote.ui.permission.PermissionStatus
import dev.seabat.ramennote.ui.permission.PermissionType
import dev.seabat.ramennote.ui.permission.createRememberedPermissionsLauncher
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import ramennote.composeapp.generated.resources.Res
import ramennote.composeapp.generated.resources.add_category_label
import ramennote.composeapp.generated.resources.add_evaluation_label
import ramennote.composeapp.generated.resources.add_map_label
import ramennote.composeapp.generated.resources.add_shop_name_label
import ramennote.composeapp.generated.resources.add_shop_no_image
import ramennote.composeapp.generated.resources.add_shop_option1
import ramennote.composeapp.generated.resources.add_shop_photo1_label
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

    Permission()

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
            ShopDropdownField(
                label = stringResource(Res.string.add_evaluation_label),
                value = star.toString(),
                onValueChange = { star = it.toIntOrNull() ?: 1 }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 最寄り駅
            ShopInputField(
                label = stringResource(Res.string.add_station_label),
                value = stationName,
                onValueChange = { stationName = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 系統
            ShopDropdownField(
                label = stringResource(Res.string.add_category_label),
                value = category,
                onValueChange = { category = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 写真1
            PhotoSelectionField(
                label = stringResource(Res.string.add_shop_photo1_label)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 登録ボタン
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
private fun Permission() {
    val currentCallback by rememberUpdatedState(
        object : PermissionCallback {
            override fun onPermissionStatus(
                permissionType: PermissionType,
                status: PermissionStatus
            ) {
                // TODO: 権限ステータスの処理
            }
        }
    )
    val permissionLauncher = createRememberedPermissionsLauncher(currentCallback)
    var shouldRequest by remember { mutableStateOf(true) }
    val isGranted = permissionLauncher.isPermissionGranted(PermissionType.GALLERY)
    if (!isGranted && shouldRequest) {
        permissionLauncher.askPermission(PermissionType.GALLERY)
        shouldRequest = false
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
    label: String
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(Res.string.add_shop_select_button),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Blue,
                modifier = Modifier.clickable { /* 画像選択処理 */ }
            )
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