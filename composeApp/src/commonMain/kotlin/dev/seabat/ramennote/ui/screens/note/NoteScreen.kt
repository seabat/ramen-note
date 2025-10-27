package dev.seabat.ramennote.ui.screens.note

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.seabat.ramennote.ui.components.AppBar
import dev.seabat.ramennote.ui.theme.RamenNoteTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import ramennote.composeapp.generated.resources.Res
import ramennote.composeapp.generated.resources.note_area_selection
import ramennote.composeapp.generated.resources.note_background
import ramennote.composeapp.generated.resources.note_item_count
import ramennote.composeapp.generated.resources.note_no_data
import ramennote.composeapp.generated.resources.screen_note_title

@Composable
fun NoteScreen(
    onAreaClick: (String) -> Unit = {},
    onAddAreaClick: () -> Unit = {},
    onAreaLongClick: (String) -> Unit = {},
    viewModel: NoteViewModelContract = koinViewModel<NoteViewModel>()
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        ScreenBar()
        MainContent(
            viewModel = viewModel,
            onAreaClick = onAreaClick,
            onAddAreaClick = onAddAreaClick,
            onAreaLongClick = onAreaLongClick
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScreenBar() {
    AppBar(title = stringResource(Res.string.screen_note_title))
}

@Composable
private fun MainContent(
    viewModel: NoteViewModelContract,
    onAreaClick: (String) -> Unit = {},
    onAddAreaClick: () -> Unit = {},
    onAreaLongClick: (String) -> Unit = {}
) {
    BoxWithConstraints(
        modifier =
            Modifier
                .fillMaxSize()
    ) {
        val imageHeight = maxHeight * 0.5f

        // ノートの背景画像（半透明、画面下方、右側1/3見切れ）
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .alpha(0.3f) // 半透明
        ) {
            // ノートの背景画像を表示
            Image(
                painter = painterResource(Res.drawable.note_background),
                contentDescription = "ノートの背景画像",
                modifier =
                    Modifier
                        .align(Alignment.BottomStart) // 画面の左下に配置
                        .offset(x = 100.dp) // 右側が見切れるように
                        .height(imageHeight),
                // 画面高さの40%
                contentScale = ContentScale.Fit
            )
        }

        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
        ) {
            LaunchedEffect(Unit) {
                viewModel.fetchAreas()
            }

            val areas by viewModel.areas.collectAsState()

            if (areas.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 88.dp) // FAB と重ならない余白
                ) {
                    item {
                        Text(
                            text = stringResource(Res.string.note_area_selection),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }

                    items(areas) { area ->
                        AreaItem(
                            areaName = area.name,
                            imageBytes = area.imageBytes,
                            itemCount = "${area.count}${stringResource(Res.string.note_item_count)}",
                            onClick = { onAreaClick(area.name) },
                            onLongClick = { onAreaLongClick(area.name) }
                        )
                    }
                }
            } else {
                Text(
                    text = stringResource(Res.string.note_no_data),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            // 右下の追加ボタン
            FloatingActionButton(
                onClick = { onAddAreaClick() },
                modifier =
                    Modifier
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
}

@Composable
private fun AreaItem(
    areaName: String,
    imageBytes: ByteArray? = null,
    itemCount: String,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {}
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(150.dp)
                .combinedClickable(onClick = onClick, onLongClick = onLongClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp),
        colors =
            CardDefaults.cardColors().copy(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
            )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // 背景画像
            if (imageBytes != null) {
                AsyncImage(
                    model = imageBytes,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // オーバーレイ（半透明の黒い背景）
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors =
                                    listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.70f)
                                    ),
                                startY = 0f,
                                endY = 500f
                            )
                        )
            )

            // コンテンツ
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                        .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = areaName,
                    style =
                        MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                    color = Color.White
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = itemCount,
                        style =
                            MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun AreaItemPreview() {
    RamenNoteTheme {
        AreaItem(areaName = "東京", imageBytes = null, itemCount = "12", onClick = {}, onLongClick = {})
    }
}

@Preview
@Composable
fun NoteScreenPreview() {
    RamenNoteTheme {
        NoteScreen(
            onAreaClick = { /* プレビュー用 */ },
            viewModel = MockNoteViewModel()
        )
    }
}
