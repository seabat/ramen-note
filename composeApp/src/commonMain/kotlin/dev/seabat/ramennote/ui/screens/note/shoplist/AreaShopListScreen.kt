package dev.seabat.ramennote.ui.screens.note.shoplist

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.seabat.ramennote.domain.model.Shop
import dev.seabat.ramennote.ui.component.AppBar
import dev.seabat.ramennote.ui.theme.RamenNoteTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import ramennote.composeapp.generated.resources.Res
import ramennote.composeapp.generated.resources.kid_star_24px_empty
import ramennote.composeapp.generated.resources.kid_star_24px_fill
import ramennote.composeapp.generated.resources.note_delete

@Composable
fun AreaShopListScreen(
    areaName: String,
    onBackClick: () -> Unit,
    onShopClick: (Shop) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        AppBar(
            title = areaName,
            onBackClick = onBackClick
        )
        AreaShopListMainContent(
            areaName = areaName,
            onShopClick = onShopClick
        )
    }
}

@Composable
private fun AreaShopListMainContent(
    areaName: String,
    onShopClick: (Shop) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // TODO: 後日SQLiteから取得する予定
        val shops = listOf(
            Shop(
                name = "XXXX家",
                shopUrl = "https://example.com/xxxx",
                mapUrl = "https://maps.google.com/xxxx",
                star = 2,
                stationName = "JR渋谷駅",
                category = "家系"
            ),
            Shop(
                name = "YYYY家",
                shopUrl = "https://example.com/yyyy",
                mapUrl = "https://maps.google.com/yyyy",
                star = 3,
                stationName = "JR新宿駅",
                category = "醤油"
            ),
            Shop(
                name = "ZZZZ家",
                shopUrl = "https://example.com/zzzz",
                mapUrl = "https://maps.google.com/zzzz",
                star = 1,
                stationName = "JR池袋駅",
                category = "味噌"
            ),
            Shop(
                name = "AAAA家",
                shopUrl = "https://example.com/aaaa",
                mapUrl = "https://maps.google.com/aaaa",
                star = 2,
                stationName = "JR上野駅",
                category = "塩"
            ),
            Shop(
                name = "BBBB家",
                shopUrl = "https://example.com/bbbb",
                mapUrl = "https://maps.google.com/bbbb",
                star = 3,
                stationName = "JR品川駅",
                category = "豚骨"
            )
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 88.dp) // FAB と重ならない余白
        ) {
            item {
                Divider(
                    color = Color.Gray.copy(alpha = 0.3f),
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
            onClick = { /* 追加処理 */ },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onError
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
                .padding(16.dp),
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
                        Icon(
                            imageVector = if (index < shop.star) {
                                vectorResource(Res.drawable.kid_star_24px_fill)
                            } else {
                                vectorResource(Res.drawable.kid_star_24px_empty)
                            },
                            contentDescription = "",
                            tint = if (index < shop.star) Color(0xFFFFEA00) else Color.White
                        )
                    }
                }

                Text(
                    text = stringResource(Res.string.note_delete),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Blue,
                    modifier = Modifier.clickable { onDelete() }
                )
            }
        }
        Divider(
            color = Color.Gray.copy(alpha = 0.3f),
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
            onShopClick = { }
        )
    }
}
