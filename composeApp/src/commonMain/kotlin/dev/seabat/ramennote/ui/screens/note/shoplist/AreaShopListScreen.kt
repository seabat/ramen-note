package dev.seabat.ramennote.ui.screens.note.shoplist

import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import dev.seabat.ramennote.ui.components.StarIcon
import org.jetbrains.compose.resources.painterResource
import dev.seabat.ramennote.ui.theme.RamenNoteTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import ramennote.composeapp.generated.resources.Res
import ramennote.composeapp.generated.resources.favorite_disabled
import ramennote.composeapp.generated.resources.favorite_enabled

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
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        AppBar(
            title = areaName,
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
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
        modifier = Modifier
            .fillMaxSize()
    ) {

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 88.dp) // FAB と重ならない余白
        ) {
            item {
                Divider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    thickness = 1.dp
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
        
        // 右下の追加ボタン
        FloatingActionButton(
            onClick = { onAddShopClick(areaName) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "追加"
            )
        }
    }
}

@Composable
private fun ShopItem(
    shop: Shop,
    onShopClick: () -> Unit,
    onDelete: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onShopClick() }
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = shop.name,
                style = MaterialTheme.typography.titleMedium
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(3) { index ->
                        StarIcon(
                            onOff = index < shop.star,
                        )
                    }
                }
                
                // お気に入りアイコン
                Image(
                    painter = painterResource(
                        if (shop.favorite) Res.drawable.favorite_enabled
                        else Res.drawable.favorite_disabled
                    ),
                    contentDescription = if (shop.favorite) "お気に入り済み" else "お気に入り未設定",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Divider(
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
            thickness = 1.dp
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
