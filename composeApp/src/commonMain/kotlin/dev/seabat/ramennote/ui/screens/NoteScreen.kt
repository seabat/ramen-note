package dev.seabat.ramennote.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
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
import org.jetbrains.compose.ui.tooling.preview.Preview
import ramennote.composeapp.generated.resources.Res
import ramennote.composeapp.generated.resources.note_area_selection
import ramennote.composeapp.generated.resources.note_delete
import ramennote.composeapp.generated.resources.note_item_count
import ramennote.composeapp.generated.resources.screen_note_title

@Composable
fun NoteScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        ScreenBar()
        MainContent()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScreenBar() {
    TopAppBar(
        // TopAppBar 内部の padding を削除する
        windowInsets = WindowInsets(0, 0, 0, 0),
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(Res.string.screen_note_title),
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = { /* 戻る処理 */ }) {
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
private fun MainContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val areas = listOf(
            "東京",
            "神奈川",
            "徳島",
            "愛媛",
            "高知",
            "福岡",
            "熊本"
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 88.dp) // FAB と重ならない余白
        ) {
            item {
                Text(
                    text = stringResource(Res.string.note_area_selection),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            items(areas) { area ->
                AreaItem(
                    areaName = area,
                    itemCount = stringResource(Res.string.note_item_count),
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
private fun AreaItem(
    areaName: String,
    itemCount: String,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clickable { /* カード全体のクリック処理 */ },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = areaName,
                style = MaterialTheme.typography.headlineSmall
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = itemCount,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                
                Text(
                    text = stringResource(Res.string.note_delete),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Blue,
                    modifier = Modifier.clickable { onDelete() }
                )
            }
        }
    }
}

@Preview
@Composable
fun NoteScreenPreview() {
    RamenNoteTheme {
        NoteScreen()
    }
}
