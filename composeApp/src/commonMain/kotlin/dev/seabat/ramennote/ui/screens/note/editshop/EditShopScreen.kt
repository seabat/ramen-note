package dev.seabat.ramennote.ui.screens.note.editshop

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.model.Shop
import dev.seabat.ramennote.ui.components.AppAlert
import dev.seabat.ramennote.ui.components.AppBar
import dev.seabat.ramennote.ui.components.MaxWidthButton
import dev.seabat.ramennote.ui.components.PhotoSelectionHandler
import dev.seabat.ramennote.ui.screens.note.shop.RamenField
import dev.seabat.ramennote.ui.screens.note.shop.ShopDropdownField
import dev.seabat.ramennote.ui.screens.note.shop.ShopInputField
import dev.seabat.ramennote.ui.screens.note.shop.ShopMultilineInputField
import dev.seabat.ramennote.ui.screens.note.shop.StarRating
import dev.seabat.ramennote.ui.theme.RamenNoteTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import ramennote.composeapp.generated.resources.Res
import ramennote.composeapp.generated.resources.add_map_label
import ramennote.composeapp.generated.resources.add_note_label
import ramennote.composeapp.generated.resources.add_shop_name_label
import ramennote.composeapp.generated.resources.add_station_label
import ramennote.composeapp.generated.resources.add_web_site_label
import ramennote.composeapp.generated.resources.edit_category_label
import ramennote.composeapp.generated.resources.edit_shop_delete_button
import ramennote.composeapp.generated.resources.edit_shop_delete_confirm
import ramennote.composeapp.generated.resources.edit_shop_delete_error_message
import ramennote.composeapp.generated.resources.edit_shop_edit_button
import ramennote.composeapp.generated.resources.edit_shop_edit_error_message
import ramennote.composeapp.generated.resources.edit_shop_title

private sealed interface ErrorDialogType {
    object Hidden : ErrorDialogType

    data class DeleteConfirm(
        val shopId: Int
    ) : ErrorDialogType
}

@Composable
fun EditShopScreen(
    shop: Shop,
    onBackClick: () -> Unit,
    onCompleted: () -> Unit,
    goToShopList: (areaName: String) -> Unit,
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
    val deleteState by viewModel.deleteState.collectAsState()
    val shopImage by viewModel.shopImage.collectAsState()

    var permissionEnabled by remember { mutableStateOf(false) }
    var errorDialogType by remember { mutableStateOf<ErrorDialogType>(ErrorDialogType.Hidden) }

    // 画像読み込み
    LaunchedEffect(shop.name) {
        viewModel.loadImage(shop)
    }

    PhotoSelectionHandler(
        onImageSelected = { viewModel.updateImage(it) },
        permissionEnabled = permissionEnabled,
        onPermissionEnabledChange = { permissionEnabled = it }
    )

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            AppBar(
                title = stringResource(Res.string.edit_shop_title),
                onBackClick = onBackClick
            )

            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(onTap = {
                                focusManager.clearFocus()
                            })
                        }
            ) {
                // 店舗名
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
                    label = stringResource(Res.string.edit_category_label),
                    value = category,
                    onValueChange = { category = "$it" }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // ノート
                ShopMultilineInputField(
                    label = stringResource(Res.string.add_note_label),
                    value = stationName,
                    onValueChange = { stationName = it }
                )

                Spacer(modifier = Modifier.height(20.dp))

                // メニュー情報
                RamenField(
                    menuName = menuName,
                    sharedImage = shopImage,
                    enablePermission = { permissionEnabled = true },
                    onMenuValueChange = { menuName = it }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // 編集ボタン
                MaxWidthButton(
                    text = stringResource(Res.string.edit_shop_edit_button),
                    enabled = name.isNotBlank(),
                    onClick = {
                        val updatedShop =
                            shop.copy(
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

                // 削除ボタン
                MaxWidthButton(
                    text = stringResource(Res.string.edit_shop_delete_button),
                    enabled = name.isNotBlank()
                ) {
                    errorDialogType = ErrorDialogType.DeleteConfirm(shop.id)
                }
            }
        }
        // 保存状態の処理
        when (saveState) {
            is RunStatus.Success -> {
                LaunchedEffect(saveState) {
                    onCompleted()
                    viewModel.setSaveStateToIdle()
                }
            }
            is RunStatus.Error -> {
                AppAlert(
                    message = saveState.message ?: stringResource(Res.string.edit_shop_edit_error_message),
                    onConfirm = { viewModel.setSaveStateToIdle() }
                )
            }
            else -> { }
        }

        // 削除状態の処理
        when (deleteState) {
            is RunStatus.Success -> {
                LaunchedEffect(deleteState) {
                    goToShopList(shop.area)
                }
            }
            is RunStatus.Error -> {
                AppAlert(
                    message = deleteState.message ?: stringResource(Res.string.edit_shop_delete_error_message),
                    onConfirm = { viewModel.setDeleteStateToIdle() }
                )
            }
            else -> { }
        }

        // エラーダイアログ
        when (val shouldShow = errorDialogType) {
            is ErrorDialogType.DeleteConfirm -> {
                AppAlert(
                    message = stringResource(Res.string.edit_shop_delete_confirm),
                    onConfirm = {
                        viewModel.deleteShop(shouldShow.shopId)
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

@Preview
@Composable
fun EditShopScreenPreview() {
    val shop =
        Shop(
            name = "XXXX家",
            area = "東京",
            shopUrl = "https://example.com",
            mapUrl = "https://maps.google.com",
            star = 2,
            stationName = "JR渋谷駅",
            category = "家系",
            menuName1 = "醤油ラーメン"
        )
    RamenNoteTheme {
        EditShopScreen(
            shop = shop,
            onBackClick = { },
            onCompleted = { },
            goToShopList = { _ -> },
            viewModel = MockEditShopViewModel()
        )
    }
}
