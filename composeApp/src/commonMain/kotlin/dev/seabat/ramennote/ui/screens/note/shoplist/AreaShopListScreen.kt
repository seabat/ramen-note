package dev.seabat.ramennote.ui.screens.note.shoplist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.seabat.ramennote.domain.model.Shop
import dev.seabat.ramennote.ui.components.AppBar
import dev.seabat.ramennote.ui.components.button.AddFab
import dev.seabat.ramennote.ui.screens.componens.ShopItem
import dev.seabat.ramennote.ui.theme.RamenNoteTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import ramennote.composeapp.generated.resources.Res
import ramennote.composeapp.generated.resources.shoplist_no_data

@Composable
fun AreaShopListScreen(
    areaName: String,
    onBackClick: () -> Unit,
    onShopClick: (Shop) -> Unit,
    onAddShopClick: (String) -> Unit,
    viewModel: AreaShopListViewModelContract = koinViewModel<AreaShopListViewModel>()
) {
    // SQLiteからデータを取得
    val shops by viewModel.shops.collectAsState()

    // 画面表示時にデータを取得（onResume相当）
    LaunchedEffect(Unit) {
        viewModel.loadShops(areaName)
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        AppBar(
            title = areaName,
            onBackClick = onBackClick
        )

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
        ) {
            AreaShopListMainContent(
                areaName = areaName,
                onShopClick = onShopClick,
                onAddShopClick = onAddShopClick,
                shops = shops
            )
        }
    }
}

@Composable
private fun AreaShopListMainContent(
    areaName: String,
    onShopClick: (Shop) -> Unit,
    onAddShopClick: (areaName: String) -> Unit,
    shops: List<Shop>
) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
    ) {
        if (shops.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 88.dp) // FAB と重ならない余白
            ) {
                item {
                    HorizontalDivider(
                        Modifier,
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                }
                items(shops) { shop ->
                    ShopItem(
                        shop = shop,
                        onShopClick = { onShopClick(shop) },
                        onDelete = { /* 削除処理 */ }
                    )
                }
            }
        } else {
            Text(
                text = stringResource(Res.string.shoplist_no_data),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        AddFab(
            modifier = Modifier.align(Alignment.BottomEnd),
            onAddClick = {
                onAddShopClick(areaName)
            }
        )
    }
}

@Preview
@Composable
fun DetailScreenPreview() {
    RamenNoteTheme {
        AreaShopListScreen(
            areaName = "東京",
            onBackClick = { },
            onShopClick = { },
            onAddShopClick = { },
            viewModel = MockAreaShopListViewModel()
        )
    }
}
