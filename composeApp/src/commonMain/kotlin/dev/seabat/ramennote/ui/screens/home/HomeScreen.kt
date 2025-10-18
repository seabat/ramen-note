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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
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
import dev.seabat.ramennote.domain.model.Schedule
import dev.seabat.ramennote.domain.model.Shop
import dev.seabat.ramennote.domain.model.FullReport
import dev.seabat.ramennote.ui.components.AppProgressBar
import dev.seabat.ramennote.ui.components.PlatformWebView
import dev.seabat.ramennote.ui.theme.RamenNoteTheme
import dev.seabat.ramennote.ui.util.createFormattedDateString
import dev.seabat.ramennote.ui.screens.history.ReportCard
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import ramennote.composeapp.generated.resources.book_5_24px
import ramennote.composeapp.generated.resources.home_background
import ramennote.composeapp.generated.resources.favorite_enabled
import ramennote.composeapp.generated.resources.home_favorite_subheading
import ramennote.composeapp.generated.resources.home_no_favorite
import ramennote.composeapp.generated.resources.home_no_reports
import ramennote.composeapp.generated.resources.home_no_web
import ramennote.composeapp.generated.resources.home_report_subheading
import ramennote.composeapp.generated.resources.ramen_dining_24px

private const val favoriteShopItemHeight = 70

@Composable
fun HomeScreen(
    goToShop: (shopId: Int, shopName: String) -> Unit = {_, _ -> },
    goToReport: (shopId: Int, shopName: String, menuName: String, iso8601Date: String) -> Unit = {_, _, _, _ -> },
    viewModel: HomeViewModelContract = koinViewModel<HomeViewModel>()
) {
    val schedule by viewModel.schedule.collectAsStateWithLifecycle()
    val scheduleState by viewModel.scheduleState.collectAsStateWithLifecycle()
    val favoriteShops by viewModel.favoriteShops.collectAsStateWithLifecycle()
    val threeMonthsReports by viewModel.threeMonthsReports.collectAsStateWithLifecycle()
    
    LaunchedEffect(Unit) {
        viewModel.loadRecentSchedule()
        viewModel.loadFavoriteShops()
        viewModel.loadThreeMonthsReports()
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
                .padding(top = 24.dp, bottom = 8.dp, start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Schedule(
                schedule,
                goToShop,
                goToReport
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 過去3ヶ月分のレポートを水平ページャーで表示
            RecentReports(
                reports = threeMonthsReports
            )

            Spacer(modifier = Modifier.height(16.dp))

            Favorite(
                favoriteShops,
                goToShop
            )
            
            if (favoriteShops.isNotEmpty()) {
                FavoriteCount(favoriteShops.size)
            }
        }

        ScheduledShopState(scheduleState) {
            viewModel.setScheduleStateToIdle()
        }
    }
}

@Composable
fun Schedule(
    schedule: Schedule?,
    goToShop: (shopId: Int, shopName: String) -> Unit = {_, _ -> },
    goToReport: (shopId: Int, shopName: String, menuName: String, iso8601Date: String) -> Unit = {_, _, _, _ -> }
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        val urlHandler = LocalUriHandler.current

        // メインのBox
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(10.dp)
                )
                .clickable {
                    schedule?.shopUrl?.let { url ->
                        if (url.isNotBlank()) {
                            urlHandler.openUri(url)
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            if (schedule != null) {
                // WebView
                if (schedule.shopUrl.isNotBlank()) {
                    PlatformWebView(
                        url = schedule.shopUrl,
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
                        Text(text = stringResource(Res.string.home_no_web))
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
            if (schedule != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = schedule.shopName,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // schedule.isReportedがtrueの時は食レポアイコンを非表示
                        if (!schedule.isReported) {
                            Icon(
                                painter = painterResource(Res.drawable.ramen_dining_24px),
                                contentDescription = "食レポ",
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable {
                                        goToReport(
                                            schedule.shopId,
                                            schedule.shopName,
                                            schedule.menuName,
                                            schedule.scheduledDate?.toString() ?: ""
                                        )
                                    },
                                tint = Color.White
                            )
                        }
                        Icon(
                            painter = painterResource(Res.drawable.book_5_24px),
                            contentDescription = "編集",
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    goToShop(schedule.shopId, schedule.shopName)
                                },
                            tint = Color.White
                        )
                    }
                }
            }
        }

        // タイトルをborder上に配置
        Text(
            text = schedule?.scheduledDate?.let {
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
fun Favorite(
    favoriteShops: List<ShopWithImage>,
    goToShop: (shopId: Int, shopName: String) -> Unit = {_,_ -> }
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(Res.string.home_favorite_subheading),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(8.dp))

        // メインのBox
        Box(
            modifier = Modifier
                .padding(horizontal = 2.dp)
                .fillMaxWidth()
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(10.dp)
                )
        ) {
            if (favoriteShops.isEmpty()) {
                Column(
                    modifier = Modifier.height(100.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = stringResource(Res.string.home_no_favorite))
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxHeight(), // 画面いっぱいまで使用
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(favoriteShops) { shopWithImage ->
                        FavoriteShopItem(
                            shop = shopWithImage.shop,
                            imageBytes = shopWithImage.imageBytes,
                            onClick = { goToShop(shopWithImage.shop.id, shopWithImage.shop.name) }
                        )
                    }
                }
            }
        }
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
    scheduleState: RunStatus<Schedule?>,
    onError: () -> Unit
) {
    when (scheduleState) {
        is RunStatus.Success -> {  /* Do nothing */ }
        is RunStatus.Error -> { /* Do nothing */ }
        is RunStatus.Loading -> { AppProgressBar() }
        is RunStatus.Idle -> { /* Do nothing */ }
    }
}


@Composable
private fun RecentReports(
    reports: List<FullReport>
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(Res.string.home_report_subheading),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (reports.isNotEmpty()) {
            // LazyRow の幅を動的に指定するために BoxWithConstraints を使用
            BoxWithConstraints(
                modifier = Modifier.fillMaxWidth()
            ) {
                val availableWidth = maxWidth
                val cardWidth = availableWidth - 30.dp // 次のカードの一部が見えるように60dp引く

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    itemsIndexed(reports) { index, report ->
                        val isLastItem = index == reports.size - 1
                        val itemWidth = if (isLastItem) {
                            availableWidth // 最後のアイテムは全幅を使用
                        } else {
                            cardWidth // 最後以外は次のカードの一部が見える幅
                        }

                        Box(
                            modifier = Modifier.width(itemWidth)
                        ) {
                            ReportCard(
                                report = report
                            )
                        }
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(horizontal = 2.dp)
                    .height(100.dp)
                    .fillMaxWidth()
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(10.dp)
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,

            ) {
                Text(text = stringResource(Res.string.home_no_reports))
            }
        }
    }
}

@Preview
@Composable
fun SchedulePreview() {
    RamenNoteTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            Schedule(Schedule())
        }
    }
}

@Preview
@Composable
fun RecentReportsPreview() {
    RamenNoteTheme {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            RecentReports(
                reports = MockHomeViewModel().threeMonthsReports.value
            )
        }
    }
}

@Preview
@Composable
fun ReportCardPreview() {
    RamenNoteTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            ReportCard(
                report = FullReport(
                    id = 1,
                    shopName = "一風堂 博多本店",
                    menuName = "白丸元味",
                    photoName = "hakata_ramen_1.jpg",
                    imageBytes = null,
                    impression = "とんこつスープが濃厚で美味しかった。麺も硬めで好みの硬さだった。",
                    date = kotlinx.datetime.LocalDate.parse("2024-12-15")
                ),
                onClick = { }
            )
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

