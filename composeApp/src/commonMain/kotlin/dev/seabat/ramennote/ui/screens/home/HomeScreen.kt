package dev.seabat.ramennote.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.ui.draw.clip
import coil3.compose.AsyncImage
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ramennote.composeapp.generated.resources.Res
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.model.Shop
import dev.seabat.ramennote.ui.components.AppProgressBar
import dev.seabat.ramennote.ui.components.PlatformWebView
import dev.seabat.ramennote.ui.theme.RamenNoteTheme
import dev.seabat.ramennote.ui.util.createFormattedDateString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import ramennote.composeapp.generated.resources.book_5_24px
import ramennote.composeapp.generated.resources.home_background
import ramennote.composeapp.generated.resources.favorite_enabled
import ramennote.composeapp.generated.resources.ramen_dining_24px

private const val favoriteShopItemHeight = 70

@Composable
fun HomeScreen(
    goToNote: (shop: Shop) -> Unit = {},
    gotToReport: (shop: Shop) -> Unit = {},
    viewModel: HomeViewModelContract = koinViewModel<HomeViewModel>()
) {
    val scheduledShop by viewModel.scheduledShop.collectAsStateWithLifecycle()
    val scheduledShopState by viewModel.scheduledShopState.collectAsStateWithLifecycle()
    val favoriteShops by viewModel.favoriteShops.collectAsStateWithLifecycle()
    
    LaunchedEffect(Unit) {
        viewModel.loadRecentSchedule()
        viewModel.loadFavoriteShops()
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val imageHeight = maxHeight * 0.5f
        
        // ラーメンの背景画像
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.3f) // 半透明
        ) {
            // ラーメンの背景画像を表示
            Image(
                painter = painterResource(Res.drawable.home_background),
                contentDescription = "ラーメンの背景画像",
                modifier = Modifier
                    .align(Alignment.BottomStart) // 画面の左下に配置
                    .offset(x = 100.dp) // 右側が見切れるように
                    .height(imageHeight),
                contentScale = ContentScale.Fit
            )
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 24.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Schedule(
                scheduledShop,
                goToNote,
                gotToReport
            )

            Spacer(modifier = Modifier.height(24.dp))

            Favorite(
                favoriteShops,
                goToNote
            )
            
            if (favoriteShops.isNotEmpty()) {
                FavoriteCount(favoriteShops.size)
            }
        }

        ScheduledShopState(scheduledShopState) {
            viewModel.setScheduledShopStateToIdle()
        }
    }
}

@Composable
fun Schedule(
    scheduledShop: Shop?,
    goToNote: (shop: Shop) -> Unit = {},
    gotToReport: (shop: Shop) -> Unit = {}
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        val urlHandler = LocalUriHandler.current

        // メインのBox
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(10.dp)
                )
                .clickable {
                    scheduledShop?.shopUrl?.let { url ->
                        if (url.isNotBlank()) {
                            urlHandler.openUri(url)
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            if (scheduledShop != null) {
                // WebView
                if (scheduledShop.shopUrl.isNotBlank()) {
                    PlatformWebView(
                        url = scheduledShop.shopUrl,
                        modifier = Modifier
                            .fillMaxSize()
                            // コンテンツの角が外側のコンポーネントからはみ出さないようにする
                            .clip(RoundedCornerShape(10.dp))
                            .align(Alignment.BottomCenter)
                    )
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "Web サイトの URLが設定されていません")
                    }
                }
            }

            // オーバーレイ（半透明の黒い背景）
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.70f)
                            ),
                            startY = 0f,
                            endY = 500f
                        ),
                        // オーバーレイの角が外側のコンポーネントからはみ出さないようにする
                        shape = RoundedCornerShape(10.dp)
                    )
            )

            // コンテンツ
            if (scheduledShop != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = scheduledShop.name,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ramen_dining_24px),
                            contentDescription = "食レポ",
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    gotToReport(scheduledShop)
                                },
                            tint = Color.White
                        )
                        Icon(
                            painter = painterResource(Res.drawable.book_5_24px),
                            contentDescription = "編集",
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    goToNote(scheduledShop)
                                },
                            tint = Color.White
                        )
                    }
                }
            }
        }

        // タイトルをborder上に配置
        Text(
            text = scheduledShop?.scheduledDate?.let {
                createFormattedDateString(it)
            } ?: "予定なし",
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 16.dp, y = (-18).dp) // 位置調整
                .background(MaterialTheme.colorScheme.background)
                .padding(4.dp),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium
        )
    }
}


@Composable
fun Favorite(favoriteShops: List<ShopWithImage>, goToNote: (shop: Shop) -> Unit = {}) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        // メインのBox
        Box(
            modifier = Modifier
                .padding(horizontal = 2.dp)
                .fillMaxWidth()
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(10.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (favoriteShops.isEmpty()) {
                Column(
                    modifier = Modifier.height(100.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "お気に入り店が登録されていません")
                }
            } else {
                // アイテム数に基づいて行数を計算（最大3行）
                val maxRows = 3
                val columns = 3
                val itemCount = favoriteShops.size
                val rowCount = minOf((itemCount + columns - 1) / columns, maxRows)
                
                LazyVerticalGrid(
                    columns = GridCells.Fixed(columns),
                    modifier = Modifier
                        .padding(12.dp)
                        .height((rowCount * favoriteShopItemHeight + (rowCount - 1) * 8 + 16).dp), // アイテム高さ + スペーシング + パディング
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(favoriteShops) { shopWithImage ->
                        FavoriteShopItem(
                            shop = shopWithImage.shop,
                            imageBytes = shopWithImage.imageBytes,
                            onClick = { goToNote(shopWithImage.shop) }
                        )
                    }
                }
            }
        }

        // タイトルをborder上に配置
        Text(
            text = "お気に入り",
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 16.dp, y = (-18).dp) // 位置調整
                .background(MaterialTheme.colorScheme.background)
                .padding(4.dp),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun FavoriteShopItem(
    shop: Shop,
    imageBytes: ByteArray?,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(8.dp)
            )
            .height(favoriteShopItemHeight.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        // 背景画像（半透明）
        if (imageBytes != null) {
            AsyncImage(
                model = imageBytes,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.3f)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        }
        
        // 店舗名テキスト
        Text(
            modifier = Modifier.padding(horizontal = 8.dp),
            text = shop.name,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            maxLines = 2,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        // 右上にfavorite_enabledアイコンを半透明で表示
        Image(
            painter = painterResource(Res.drawable.favorite_enabled),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(6.dp)
                .alpha(0.4f)
                .size(12.dp)
        )
    }
}

@Composable
fun FavoriteCount(count: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "${count}件",
            modifier = Modifier.align(Alignment.CenterEnd),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ScheduledShopState(
    scheduledShopState: RunStatus<Shop?>,
    onError: () -> Unit
) {
    when (scheduledShopState) {
        is RunStatus.Success -> {  /* Do nothing */ }
        is RunStatus.Error -> { /* Do nothing */ }
        is RunStatus.Loading -> { AppProgressBar() }
        is RunStatus.Idle -> { /* Do nothing */ }
    }
}

@Preview
@Composable
fun SchedulePreview() {
    RamenNoteTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            Schedule(Shop())
        }
    }
}

@Preview
@Composable
fun FavoritePreview() {
    RamenNoteTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            Favorite(
                MockHomeViewModel().favoriteShops.value,
            )
        }
    }
}

