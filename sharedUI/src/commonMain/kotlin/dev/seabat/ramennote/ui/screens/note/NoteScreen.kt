package dev.seabat.ramennote.ui.screens.note

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.seabat.ramennote.domain.model.Shop
import dev.seabat.ramennote.ui.components.AppBar
import dev.seabat.ramennote.ui.components.banner.HintBanner
import dev.seabat.ramennote.ui.components.button.ActionButton
import dev.seabat.ramennote.ui.components.button.AddFab
import dev.seabat.ramennote.ui.components.button.ReportListButton
import dev.seabat.ramennote.ui.screens.componens.ShopItem
import dev.seabat.ramennote.ui.screens.note.shop.SearchInputField
import dev.seabat.ramennote.ui.theme.RamenNoteTheme
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import ramennote.sharedui.generated.resources.Res
import ramennote.sharedui.generated.resources.location_on_24px
import ramennote.sharedui.generated.resources.note_background
import ramennote.sharedui.generated.resources.note_hit_count
import ramennote.sharedui.generated.resources.note_item_count
import ramennote.sharedui.generated.resources.note_map_button
import ramennote.sharedui.generated.resources.note_no_data
import ramennote.sharedui.generated.resources.note_notification
import ramennote.sharedui.generated.resources.note_search_hint
import ramennote.sharedui.generated.resources.note_sort
import ramennote.sharedui.generated.resources.screen_note_title
import ramennote.sharedui.generated.resources.sort_24px

@Composable
fun NoteScreen(
    initialSearchText: String = "",
    goToAreaShopList: (String) -> Unit = {},
    goToAddArea: () -> Unit = {},
    goToEditArea: (Int) -> Unit = {},
    goToEditAreaSort: () -> Unit = {},
    goToShop: (Shop) -> Unit = {},
    goToHistoryWithAreaFiltering: (areaId: Int) -> Unit = {},
    goToShopsLocation: (areaId: Int, areaName: String) -> Unit = { _, _ -> },
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
            onAreaClick = goToAreaShopList,
            onAddAreaClick = goToAddArea,
            onAreaLongClick = goToEditArea,
            onSortClick = goToEditAreaSort,
            onShopClick = goToShop,
            onAreaReportClick = goToHistoryWithAreaFiltering,
            onMapClick = goToShopsLocation,
            initialSearchText = initialSearchText
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
    onAreaLongClick: (Int) -> Unit = {},
    onSortClick: () -> Unit = {},
    onShopClick: (Shop) -> Unit = {},
    onAreaReportClick: (areaId: Int) -> Unit = {},
    onMapClick: (areaId: Int, areaName: String) -> Unit = { _, _ -> },
    initialSearchText: String = ""
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
            var isBannerVisible by remember { mutableStateOf(true) }
            var isSearchResultVisible by remember { mutableStateOf(initialSearchText.isNotEmpty()) }
            var searchText by remember { mutableStateOf(initialSearchText) }

            LaunchedEffect(Unit) {
                viewModel.fetchAreas()
                if (searchText.isNotEmpty()) {
                    viewModel.searchShops(searchText)
                }
                delay(3000)
                isBannerVisible = false
            }

            val areas by viewModel.areas.collectAsState()
            val shops by viewModel.shops.collectAsState()

            if (areas.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 88.dp) // FAB と重ならない余白
                ) {
                    item {
                        Menu(
                            searchText = searchText,
                            onSortClick = onSortClick,
                            onSearchTextChange = { text ->
                                searchText = text
                                isSearchResultVisible = text.isNotEmpty()
                                if (text.isNotEmpty()) {
                                    viewModel.searchShops(text)
                                }
                            }
                        )
                    }
                    if (isSearchResultVisible) {
                        item {
                            Column {
                                Text(
                                    text = stringResource(Res.string.note_hit_count, shops.size),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                if (shops.isNotEmpty()) {
                                    HorizontalDivider(
                                        Modifier,
                                        1.dp,
                                        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                    )
                                }
                            }
                        }
                        items(shops) { shop ->
                            ShopItem(
                                shop = shop,
                                onShopClick = { onShopClick(shop) },
                                onDelete = { /* 削除処理 */ }
                            )
                        }
                    } else {
                        item {
                            HintBanner(
                                isVisible = isBannerVisible,
                                text = stringResource(Res.string.note_notification)
                            )
                        }

                        itemsIndexed(areas) { index, area ->
                            AnimatedVisibility(
                                visibleState =
                                    remember {
                                        MutableTransitionState(false).apply { targetState = true }
                                    },
                                enter =
                                    slideInHorizontally(
                                        animationSpec =
                                            tween(
                                                durationMillis = 500,
                                                delayMillis = if (index < 10) index * 100 else 0
                                            ),
                                        initialOffsetX = { it }
                                    ) +
                                        fadeIn(
                                            animationSpec =
                                                tween(
                                                    durationMillis = 500,
                                                    delayMillis = if (index < 10) index * 100 else 0
                                                )
                                        )
                            ) {
                                AreaItem(
                                    areaName = area.name,
                                    imagePath = area.imagePath,
                                    itemCount = "${area.count}${stringResource(Res.string.note_item_count)}",
                                    onClick = { onAreaClick(area.name) },
                                    onLongClick = { onAreaLongClick(area.areaId) },
                                    onReportClick = { onAreaReportClick(area.areaId) },
                                    onMapClick = { onMapClick(area.areaId, area.name) }
                                )
                            }
                        }
                    }
                }
            } else {
                Text(
                    text = stringResource(Res.string.note_no_data),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            AddFab(
                modifier = Modifier.align(Alignment.BottomEnd),
                onAddClick = {
                    onAddAreaClick()
                }
            )
        }
    }
}

@Composable
private fun Menu(
    searchText: String,
    onSortClick: () -> Unit = {},
    onSearchTextChange: (String) -> Unit
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 0.dp, vertical = 4.dp),
//        horizontalArrangement = Arrangement.spacedBy(8.dp)
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SortButton(onClick = onSortClick)
        Spacer(modifier = Modifier.width(16.dp))
        SearchInputField(
            placeholder = stringResource(Res.string.note_search_hint),
            value = searchText,
            onValueChange = onSearchTextChange
        )
    }
}

@Composable
private fun SortButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ActionButton(
        icon = vectorResource(Res.drawable.sort_24px),
        text = stringResource(Res.string.note_sort),
        onClick = onClick,
        modifier = modifier
    )
}

@Composable
private fun AreaItem(
    areaName: String,
    imagePath: String? = null,
    itemCount: String,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {},
    onReportClick: () -> Unit = {},
    onMapClick: () -> Unit = {}
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
            if (imagePath != null) {
                AsyncImage(
                    model = imagePath,
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
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = itemCount,
                        style =
                            MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    ActionButton(
                        icon = vectorResource(Res.drawable.location_on_24px),
                        text = stringResource(Res.string.note_map_button),
                        onClick = onMapClick
                    )
                    ReportListButton(onClick = onReportClick)
                }
            }
        }
    }
}

@Preview
@Composable
fun AreaItemPreview() {
    RamenNoteTheme {
        AreaItem(areaName = "東京", imagePath = null, itemCount = "12件", onClick = {}, onLongClick = {})
    }
}

@Preview
@Composable
fun NoteScreenForAreaPreview() {
    RamenNoteTheme {
        NoteScreen(
            goToAreaShopList = { /* プレビュー用 */ },
            viewModel = MockNoteViewModel(),
            initialSearchText = ""
        )
    }
}

@Preview
@Composable
fun NoteScreenForSearchPreview() {
    RamenNoteTheme {
        NoteScreen(
            goToAreaShopList = { /* プレビュー用 */ },
            viewModel = MockNoteViewModel(),
            initialSearchText = "一風堂"
        )
    }
}
