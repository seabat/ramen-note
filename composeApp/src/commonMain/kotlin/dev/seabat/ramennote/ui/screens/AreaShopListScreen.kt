package dev.seabat.ramennote.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.seabat.ramennote.ui.theme.RamenNoteTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import ramennote.composeapp.generated.resources.Res
import ramennote.composeapp.generated.resources.kid_star_24px_empty
import ramennote.composeapp.generated.resources.kid_star_24px_fill
import ramennote.composeapp.generated.resources.note_delete
import ramennote.composeapp.generated.resources.note_item_count
import ramennote.composeapp.generated.resources.signpost_24px

@Composable
fun AreaShopListScreen(
    areaName: String,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        AreaShopListBar(
            areaName = areaName,
            onBackClick = onBackClick
        )
        AreaShopListMainContent(areaName = areaName)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AreaShopListBar(
    areaName: String,
    onBackClick: () -> Unit
) {
    TopAppBar(
        windowInsets = WindowInsets(0, 0, 0, 0),
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = areaName,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "戻る",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        actions = {
            // 右側のバランスを取るための透明なスペーサー
            IconButton(onClick = { /* 何もしない */ }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "",
                    tint = Color.Transparent
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.primary,
            navigationIconContentColor = MaterialTheme.colorScheme.background
        )
    )
}

@Composable
private fun AreaShopListMainContent(areaName: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val restaurants = listOf(
            "XXXX家",
            "YYYY家", 
            "ZZZZ家",
            "AAAA家",
            "BBBB家"
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
            items(restaurants) { restaurant ->
                ShopItem(
                    restaurantName = restaurant,
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
            containerColor = Color(0xFFD32F2F), // 赤色
            contentColor = Color.White
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
    restaurantName: String,
    onDelete: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { /* レストラン詳細のクリック処理 */ }
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = restaurantName,
                style = MaterialTheme.typography.titleMedium
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.kid_star_24px_fill),
                        contentDescription = "",
                        tint = Color (0xFFFFEA00)
                    )
                    Icon(
                        imageVector = vectorResource(Res.drawable.kid_star_24px_fill),
                        contentDescription = "",
                        tint = Color (0xFFFFEA00)
                    )
                    Icon(
                        imageVector = vectorResource(Res.drawable.kid_star_24px_empty),
                        contentDescription = "",
                        tint = Color (0xFFFFFEA00)
                    )
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
            onBackClick = { }
        )
    }
}
