package dev.seabat.ramennote.ui.screens

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
    onBackClick: () -> Unit
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
        AreaShopListMainContent(areaName = areaName)
    }
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
