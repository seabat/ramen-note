package dev.seabat.ramennote.ui.components.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.seabat.ramennote.domain.model.Area
import dev.seabat.ramennote.domain.model.Shop
import dev.seabat.ramennote.ui.components.dropdown.DropdownAnchorField
import dev.seabat.ramennote.ui.screens.history.MockSelectShopViewModel
import dev.seabat.ramennote.ui.screens.history.MockSelectShopViewModelWithData
import dev.seabat.ramennote.ui.screens.history.SelectShopViewModel
import dev.seabat.ramennote.ui.screens.history.SelectShopViewModelContract
import dev.seabat.ramennote.ui.theme.RamenNoteTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import ramennote.sharedui.generated.resources.Res
import ramennote.sharedui.generated.resources.select_shop_dialog_area_label
import ramennote.sharedui.generated.resources.select_shop_dialog_cancel_button
import ramennote.sharedui.generated.resources.select_shop_dialog_shop_label
import ramennote.sharedui.generated.resources.select_shop_dialog_title
import ramennote.sharedui.generated.resources.select_shop_dialog_write_report_button

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectShopDialog(
    onDismiss: () -> Unit,
    confirmButtonLabel: String,
    onConfirmClick: (Shop) -> Unit,
    viewModel: SelectShopViewModelContract = koinViewModel<SelectShopViewModel>()
) {
    val areas by viewModel.areas.collectAsStateWithLifecycle()
    val shopsInArea by viewModel.shopsInArea.collectAsStateWithLifecycle()

    var selectedArea by remember { mutableStateOf<Area?>(null) }
    var selectedShop by remember { mutableStateOf<Shop?>(null) }
    var areaExpanded by remember { mutableStateOf(false) }
    var shopExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { viewModel.loadAreas() }

    WideDialog(onDismiss = onDismiss) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
        ) {
            Text(
                text = stringResource(Res.string.select_shop_dialog_title),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // エリアドロップダウン
            Column(modifier = Modifier.semantics(mergeDescendants = true) {}) {
                Text(
                    text = stringResource(Res.string.select_shop_dialog_area_label),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                ExposedDropdownMenuBox(
                    expanded = areaExpanded,
                    onExpandedChange = { areaExpanded = !areaExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    DropdownAnchorField(
                        text = selectedArea?.name ?: "",
                        expanded = areaExpanded,
                        onToggle = { areaExpanded = !areaExpanded },
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                    )
                    ExposedDropdownMenu(
                        expanded = areaExpanded,
                        onDismissRequest = { areaExpanded = false }
                    ) {
                        areas.forEach { area ->
                            DropdownMenuItem(
                                text = { Text(area.name) },
                                onClick = {
                                    selectedArea = area
                                    selectedShop = null
                                    viewModel.loadShopsByArea(area.name)
                                    areaExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 店舗ドロップダウン（エリア未選択時はグレーアウト）
            val shopEnabled = selectedArea != null
            Column(
                modifier =
                    Modifier
                        .semantics(mergeDescendants = true) {}
                        .alpha(if (shopEnabled) 1f else 0.38f)
            ) {
                Text(
                    text = stringResource(Res.string.select_shop_dialog_shop_label),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                ExposedDropdownMenuBox(
                    expanded = shopExpanded,
                    onExpandedChange = { if (shopEnabled) shopExpanded = !shopExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    DropdownAnchorField(
                        text = selectedShop?.name ?: "",
                        expanded = shopExpanded,
                        onToggle = { if (shopEnabled) shopExpanded = !shopExpanded },
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = shopEnabled)
                    )
                    ExposedDropdownMenu(
                        expanded = shopExpanded,
                        onDismissRequest = { shopExpanded = false }
                    ) {
                        shopsInArea.forEach { shop ->
                            DropdownMenuItem(
                                text = { Text(shop.name) },
                                onClick = {
                                    selectedShop = shop
                                    shopExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ボタン行
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(Res.string.select_shop_dialog_cancel_button))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { selectedShop?.let { onConfirmClick(it) } },
                    enabled = selectedShop != null
                ) {
                    Text(confirmButtonLabel)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Preview
@Composable
private fun SelectShopDialogPreview() {
    RamenNoteTheme {
        SelectShopDialog(
            onDismiss = {},
            confirmButtonLabel = stringResource(Res.string.select_shop_dialog_write_report_button),
            onConfirmClick = {},
            viewModel = MockSelectShopViewModel()
        )
    }
}

@Preview
@Composable
private fun SelectShopDialogWithDataPreview() {
    RamenNoteTheme {
        SelectShopDialog(
            onDismiss = {},
            confirmButtonLabel = stringResource(Res.string.select_shop_dialog_write_report_button),
            onConfirmClick = {},
            viewModel = MockSelectShopViewModelWithData()
        )
    }
}
