package dev.seabat.ramennote.ui.screens.history

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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.seabat.ramennote.domain.model.Area
import dev.seabat.ramennote.domain.model.Shop
import dev.seabat.ramennote.ui.components.dialog.WideDialog
import dev.seabat.ramennote.ui.screens.componens.DropdownAnchorField
import dev.seabat.ramennote.ui.theme.RamenNoteTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectShopDialog(
    onDismiss: () -> Unit,
    onReportClick: (Shop) -> Unit,
    viewModel: SelectShopViewModelContract = koinViewModel<SelectShopViewModel>()
) {
    val areas by viewModel.areas.collectAsState()
    val shopsInArea by viewModel.shopsInArea.collectAsState()

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
                text = "店舗を選択",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // エリアドロップダウン
            // label を DropdownAnchorField 内に持つため外側ラベルは不要
            ExposedDropdownMenuBox(
                expanded = areaExpanded,
                onExpandedChange = { areaExpanded = !areaExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                DropdownAnchorField(
                    text = selectedArea?.name ?: "",
                    label = "エリア",
                    expanded = areaExpanded,
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

            Spacer(modifier = Modifier.height(16.dp))

            // 店舗ドロップダウン（エリア未選択時は enabled = false で MD3 の disabled スタイルを自動適用）
            val shopEnabled = selectedArea != null
            ExposedDropdownMenuBox(
                expanded = shopExpanded,
                onExpandedChange = { if (shopEnabled) shopExpanded = !shopExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                DropdownAnchorField(
                    text = selectedShop?.name ?: "",
                    label = "店舗",
                    expanded = shopExpanded,
                    enabled = shopEnabled,
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

            Spacer(modifier = Modifier.height(24.dp))

            // ボタン行
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.weight(1f))
                OutlinedButton(onClick = onDismiss) {
                    Text("キャンセル")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { selectedShop?.let { onReportClick(it) } },
                    enabled = selectedShop != null
                ) {
                    Text("レポートを書く")
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
            onReportClick = {},
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
            onReportClick = {},
            viewModel = MockSelectShopViewModelWithData()
        )
    }
}
