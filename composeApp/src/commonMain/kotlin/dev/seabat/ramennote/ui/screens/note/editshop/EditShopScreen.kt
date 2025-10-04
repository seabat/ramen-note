package dev.seabat.ramennote.ui.screens.note.editshop

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.model.Shop
import dev.seabat.ramennote.domain.util.logd
import dev.seabat.ramennote.ui.component.AppBar
import dev.seabat.ramennote.ui.component.AppAlert
import dev.seabat.ramennote.ui.component.AppTwoButtonAlert
import dev.seabat.ramennote.ui.component.MaxWidthButton
import dev.seabat.ramennote.ui.component.StarIcon
import dev.seabat.ramennote.ui.gallery.SharedImage
import dev.seabat.ramennote.ui.gallery.createRememberedGalleryLauncher
import dev.seabat.ramennote.ui.permission.PermissionCallback
import dev.seabat.ramennote.ui.permission.PermissionStatus
import dev.seabat.ramennote.ui.permission.PermissionType
import dev.seabat.ramennote.ui.permission.createRememberedPermissionsLauncher
import dev.seabat.ramennote.ui.permission.launchSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import ramennote.composeapp.generated.resources.Res
import ramennote.composeapp.generated.resources.add_evaluation_label
import ramennote.composeapp.generated.resources.add_map_label
import ramennote.composeapp.generated.resources.add_shop_menu_name_label
import ramennote.composeapp.generated.resources.add_shop_name_label
import ramennote.composeapp.generated.resources.add_shop_no_image
import ramennote.composeapp.generated.resources.add_shop_photo_label
import ramennote.composeapp.generated.resources.add_shop_select_button
import ramennote.composeapp.generated.resources.add_shop_title
import ramennote.composeapp.generated.resources.add_station_label
import ramennote.composeapp.generated.resources.add_web_site_label

@Composable
fun EditShopScreen(
    shop: Shop,
    onBackClick: () -> Unit,
    onCompleted: () -> Unit,
    viewModel: EditShopViewModelContract = koinViewModel<EditShopViewModel>()
) {

    var name by remember { mutableStateOf(shop.name) }
    var shopUrl by remember { mutableStateOf(shop.shopUrl) }
    var mapUrl by remember { mutableStateOf(shop.mapUrl) }
    var star by remember { mutableStateOf(shop.star) }
    var stationName by remember { mutableStateOf(shop.stationName) }
    val focusManager = LocalFocusManager.current
    var category by remember { mutableStateOf(shop.category) }
    var menuName by remember { mutableStateOf(shop.menuName1) }

    val saveState by viewModel.saveState.collectAsState()
    val shopImage by viewModel.shopImage.collectAsState()

    var permissionEnabled by remember { mutableStateOf(false) }
    var shouldLaunchSetting by remember { mutableStateOf(false) }
    var shouldShowPermissionRationalDialog by remember { mutableStateOf(false) }
    val galleryManager = createRememberedGalleryLauncher { viewModel.updateImage(it) }

    // 画像読み込み
    LaunchedEffect(shop.name) {
        viewModel.loadImage(shop)
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

    Scaffold(
        topBar = {
            AppBar(
                title = stringResource(Res.string.add_shop_title),
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = {
                            focusManager.clearFocus()
                        })
                    }
            ) {
                // 店舗名
                InputField(
                    label = stringResource(Res.string.add_shop_name_label),
                    value = name,
                    onValueChange = { name = it },
                    onDone = { focusManager.clearFocus() }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Webサイト
                InputField(
                    label = stringResource(Res.string.add_web_site_label),
                    value = shopUrl,
                    onValueChange = { shopUrl = it },
                    onDone = { focusManager.clearFocus() }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 地図
                InputField(
                    label = stringResource(Res.string.add_map_label),
                    value = mapUrl,
                    onValueChange = { mapUrl = it },
                    onDone = { focusManager.clearFocus() }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 評価
                StarRating(
                    star = star,
                    onValueChange = { star = it }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 最寄り駅
                InputField(
                    label = stringResource(Res.string.add_station_label),
                    value = stationName,
                    onValueChange = { stationName = it },
                    onDone = { focusManager.clearFocus() }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // メニュー名
                InputField(
                    label = stringResource(Res.string.add_shop_menu_name_label),
                    value = menuName,
                    onValueChange = { menuName = it },
                    onDone = { focusManager.clearFocus() }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // ラーメン
                Ramen(
                    menuName = menuName,
                    sharedImage = shopImage,
                    enablePermission = { permissionEnabled = true },
                    onMenuValueChange = { menuName = it }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // 編集ボタン
                MaxWidthButton(
                    text = "編集する",
                    onClick = {
                        val updatedShop = shop.copy(
                            name = name,
                            shopUrl = shopUrl,
                            mapUrl = mapUrl,
                            star = star,
                            stationName = stationName,
                            category = category,
                            menuName1 = menuName
                        )
                        viewModel.updateShop(updatedShop, shopImage)
                    }
                )
            }
        }
    }

    // 保存状態の処理
    when (saveState) {
        is RunStatus.Success -> {
            LaunchedEffect(saveState) {
                onCompleted()
            }
        }
        is RunStatus.Error -> {
            AppAlert(
                message = saveState.message ?: "不明なエラーが発生しました",
                onConfirm = { /* エラー処理 */ }
            )
        }
        else -> { }
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
private fun InputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    onDone: () -> Unit
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // OutlinedTextField は内部パディングが大きいので BasicTextField で代用
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(4.dp)
                )
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onDone() })
            )
        }
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

@Composable
private fun ImageSelection(
    label: String,
    sharedImage: SharedImage?,
    shopImage: ByteArray?,
    onPermissionRequest: () -> Unit
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable { onPermissionRequest() }
        ) {
            if (sharedImage != null) {
                val imageBitmap = sharedImage.toImageBitmap()
                if (imageBitmap != null) {
                    Image(
                        bitmap = imageBitmap,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            } else if (shopImage != null) {
                // 既存の画像を表示
                Text(
                    text = "既存の画像",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                Text(
                    text = stringResource(Res.string.add_shop_no_image),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Button(
            onClick = { onPermissionRequest() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(Res.string.add_shop_select_button))
        }
    }
}

@Preview
@Composable
fun EditShopScreenPreview() {
    val shop = Shop(
        name = "XXXX家",
        area = "東京",
        shopUrl = "https://example.com",
        mapUrl = "https://maps.google.com",
        star = 2,
        stationName = "JR渋谷駅",
        category = "家系",
        menuName1 = "醤油ラーメン"
    )
    
    EditShopScreen(
        shop = shop,
        onBackClick = { },
        onCompleted = { },
        viewModel = MockEditShopViewModel()
    )
}

@Composable
private fun Ramen(
    menuName: String = "",
    sharedImage: SharedImage?,
    enablePermission: () -> Unit,
    onMenuValueChange: (String) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        // メインのBox
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                ShopInputField(
                    label = stringResource(Res.string.add_shop_menu_name_label),
                    value = menuName,
                    onValueChange = onMenuValueChange
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 写真
                PhotoSelectionField(
                    label = stringResource(Res.string.add_shop_photo_label),
                    sharedImage = sharedImage,
                    onClick = enablePermission
                )
            }
        }
        
        // タイトルをborder上に配置
        Text(
            text = "メニュー情報",
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 16.dp, y = (-8).dp) // 位置調整
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 4.dp),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun ShopInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current
    
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
        
        Spacer(modifier = Modifier.height(4.dp))

        // OutlinedTextField は内部パディングが大きいので BasicTextField で代用
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(4.dp)
                )
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                )
            )
        }
    }
}

@Composable
private fun PhotoSelectionField(
    label: String,
    sharedImage: SharedImage? = null,
    onClick: () -> Unit
) {
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    LaunchedEffect(sharedImage) {
        val image = withContext(Dispatchers.IO) {
            sharedImage?.toImageBitmap()
        }
        imageBitmap = image
    }

    Column {
        // 写真
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 写真選択
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(Res.string.add_shop_select_button),
                style = MaterialTheme.typography.titleMedium,
                color = Color.Blue,
                modifier = Modifier.clickable { onClick() }
            )
            if (imageBitmap != null) {
                Image(
                    modifier = Modifier
                        .size(120.dp),
                    bitmap = imageBitmap!!,
                    contentDescription = null
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
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
