package dev.seabat.ramennote.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import dev.seabat.ramennote.domain.model.FullReport
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.model.Schedule
import dev.seabat.ramennote.domain.model.Shop
import dev.seabat.ramennote.domain.util.logd
import dev.seabat.ramennote.ui.components.AppProgressBar
import dev.seabat.ramennote.ui.components.PlatformWebView
import dev.seabat.ramennote.ui.components.rememberLifecycleState
import dev.seabat.ramennote.ui.screens.history.ReportCard
import dev.seabat.ramennote.ui.theme.RamenNoteTheme
import dev.seabat.ramennote.ui.util.createFormattedDateString
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.takeWhile
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import ramennote.composeapp.generated.resources.Res
import ramennote.composeapp.generated.resources.book_5_24px
import ramennote.composeapp.generated.resources.favorite_enabled
import ramennote.composeapp.generated.resources.home_background
import ramennote.composeapp.generated.resources.home_favorite_subheading
import ramennote.composeapp.generated.resources.home_no_favorite
import ramennote.composeapp.generated.resources.home_no_reports
import ramennote.composeapp.generated.resources.home_no_web
import ramennote.composeapp.generated.resources.home_report_subheading
import ramennote.composeapp.generated.resources.ramen_dining_24px
import kotlin.collections.filter

private const val FAVORITE_SHOP_ITEM_HEIGHT = 70

@Composable
fun HomeScreen(
    goToShop: (shopId: Int, shopName: String) -> Unit = { _, _ -> },
    goToReport: (shopId: Int, shopName: String, menuName: String, iso8601Date: String) -> Unit = { _, _, _, _ -> },
    goToHistory: (reportId: Int) -> Unit = { _ -> },
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
        modifier = Modifier.fillMaxSize()
    ) {
        val imageHeight = maxHeight * 0.5f

        HomeBackground(imageHeight)

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp, bottom = 8.dp, start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Schedule(
                schedule,
                favoriteShops,
                goToShop,
                goToReport
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 過去3ヶ月分のレポートを水平ページャーで表示
            RecentReports(
                reports = threeMonthsReports,
                goToHistory = goToHistory
            )

            Spacer(modifier = Modifier.height(8.dp))

            Favorite(
                favoriteShops,
                goToShop
            )
        }

        ScheduledShopState(scheduleState) {
            viewModel.setScheduleStateToIdle()
        }
    }
}

@Composable
private fun HomeBackground(imageHeight: Dp) {
    // ラーメンの背景画像
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .alpha(0.3f) // 半透明
    ) {
        // ラーメンの背景画像を表示
        Image(
            painter = painterResource(Res.drawable.home_background),
            contentDescription = "ラーメンの背景画像",
            modifier =
                Modifier
                    .align(Alignment.BottomStart) // 画面の左下に配置
                    .offset(x = 100.dp) // 右側が見切れるように
                    .height(imageHeight),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
private fun Schedule(
    schedule: Schedule?,
    favoriteShops: List<ShopWithImage>,
    goToShop: (shopId: Int, shopName: String) -> Unit = { _, _ -> },
    goToReport: (shopId: Int, shopName: String, menuName: String, iso8601Date: String) -> Unit = { _, _, _, _ -> }
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        val urlHandler = LocalUriHandler.current

        Text(
            text =
                schedule?.scheduledDate?.let {
                    "${createFormattedDateString(it)}の予定"
                } ?: "お気に入り店のWebサイト",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.secondary
        )

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(216.dp)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(10.dp)
                    ).clickable {
                        schedule?.shopUrl?.let { url ->
                            if (url.isNotBlank()) {
                                urlHandler.openUri(url)
                            }
                        }
                    }
        ) {
            // コンテンツ
            if (schedule == null) {
                FavoriteShopsWeb(Modifier.align(Alignment.BottomCenter), favoriteShops)
            } else {
                ScheduledShopWeb(Modifier.align(Alignment.BottomCenter), schedule)
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
                            ),
                            // オーバーレイの角が外側のコンポーネントからはみ出さないようにする
                            shape = RoundedCornerShape(10.dp)
                        )
            )

            // メニュー
            if (schedule != null) {
                ScheduleMenu(
                    Modifier.align(Alignment.BottomStart),
                    schedule,
                    goToShop,
                    goToReport
                )
            }
        }
    }
}

@Composable
private fun ScheduledShopWeb(
    modifier: Modifier,
    schedule: Schedule
) {
    // WebView
    if (schedule.shopUrl.isNotBlank()) {
        PlatformWebView(
            url = schedule.shopUrl,
            modifier =
                modifier
                    .fillMaxSize()
                    // コンテンツの角が外側のコンポーネントからはみ出さないようにする
                    .clip(RoundedCornerShape(10.dp))
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

@Composable
private fun FavoriteShopsWeb(
    modifier: Modifier,
    favoriteShops: List<ShopWithImage>
) {
    // favoriteShopsのshopUrlを10秒ごとに切り替えながら表示
    val validUrls =
        favoriteShops
            .map { it.shop.shopUrl }
            .filter { it.isNotBlank() }

    if (validUrls.isNotEmpty()) {
        var currentIndex by remember(validUrls) { mutableIntStateOf(0) }
        val currentUrl = validUrls[currentIndex]
        val lifecycleState = rememberLifecycleState()

        // 10秒ごとにインデックスを更新
        // フォアグラウンドの時のみ更新処理が動作する
        LaunchedEffect(validUrls.size) {
            snapshotFlow {
                lifecycleState.value.isResumed
            }.distinctUntilChanged()
                .collect { isResumed ->
                    logd(message = "collect isResumed($isResumed)")
                    if (isResumed) {
                        // フォアグラウンドになったら定期的なFlowを開始
                        // takeWhileでlifecycleState.isResumedがfalseになったら自動的に停止
                        flow {
                            while (true) {
                                logd(message = "Home Timer emit")
                                emit(Unit)
                                delay(30000) // 30秒待機
                            }
                        }.takeWhile { lifecycleState.value.isResumed }
                            .collect {
                                logd(message = "Home Timer collect after takeWhile")
                                currentIndex = (currentIndex + 1) % validUrls.size
                            }
                    }
                }
        }

        // keyを使ってURLが変更されたときにWebViewを再作成
        key(currentUrl) {
            PlatformWebView(
                url = currentUrl,
                modifier =
                    modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(10.dp))
            )
        }
    }
}

@Composable
private fun ScheduleMenu(
    modifier: Modifier,
    schedule: Schedule,
    goToShop: (shopId: Int, shopName: String) -> Unit = { _, _ -> },
    goToReport: (shopId: Int, shopName: String, menuName: String, iso8601Date: String) -> Unit = { _, _, _, _ -> }
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = schedule.shopName,
            style =
                MaterialTheme.typography.titleMedium.copy(
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
                    modifier =
                        Modifier
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
                modifier =
                    Modifier
                        .size(24.dp)
                        .clickable {
                            goToShop(schedule.shopId, schedule.shopName)
                        },
                tint = Color.White
            )
        }
    }
}

@Composable
private fun Favorite(
    favoriteShops: List<ShopWithImage>,
    goToShop: (shopId: Int, shopName: String) -> Unit = { _, _ -> }
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(Res.string.home_favorite_subheading),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary
            )

            // お気に入り店数
            if (favoriteShops.isNotEmpty()) {
                Text(
                    modifier = Modifier.padding(end = 8.dp),
                    text = "${favoriteShops.size}件",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // メインのBox
        Box(
            modifier =
                Modifier
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
                    modifier =
                        Modifier
                            .padding(4.dp)
                            .fillMaxHeight(),
                    // 画面いっぱいまで使用
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
private fun FavoriteShopItem(
    shop: Shop,
    imageBytes: ByteArray?,
    onClick: () -> Unit
) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(8.dp)
                ).height(FAVORITE_SHOP_ITEM_HEIGHT.dp)
                .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        // 背景画像（半透明）
        if (imageBytes != null) {
            AsyncImage(
                model = imageBytes,
                contentDescription = null,
                modifier =
                    Modifier
                        .fillMaxSize()
                        .alpha(0.3f)
                        .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        }

        // 店舗名テキスト
        Text(
            modifier =
                Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 8.dp),
            text = shop.name,
            style =
                MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface
        )

        // 右上にfavorite_enabledアイコンを半透明で表示
        Image(
            painter = painterResource(Res.drawable.favorite_enabled),
            contentDescription = null,
            modifier =
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .alpha(0.4f)
                    .size(12.dp)
        )
    }
}

@Composable
private fun ScheduledShopState(
    scheduleState: RunStatus<Schedule?>,
    onError: () -> Unit
) {
    when (scheduleState) {
        is RunStatus.Success -> { /* Do nothing */ }
        is RunStatus.Error -> { /* Do nothing */ }
        is RunStatus.Loading -> {
            AppProgressBar()
        }
        is RunStatus.Idle -> { /* Do nothing */ }
    }
}

@Composable
private fun RecentReports(
    reports: List<FullReport>,
    goToHistory: (reportId: Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(Res.string.home_report_subheading),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.secondary
        )

        if (reports.isNotEmpty()) {
            // LazyRow の幅を動的に指定するために BoxWithConstraints を使用
            BoxWithConstraints(
                modifier = Modifier.fillMaxWidth()
            ) {
                val availableWidth = maxWidth
                val cardWidth = availableWidth - 30.dp // 次のカードの一部が見えるように30dp引く

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier =
                        Modifier
                            .fillMaxWidth()
                ) {
                    itemsIndexed(reports) { index, report ->
                        val isLastItem = index == reports.size - 1
                        val itemWidth =
                            if (isLastItem) {
                                availableWidth // 最後のアイテムは全幅を使用
                            } else {
                                cardWidth // 最後以外は次のカードの一部が見える幅
                            }

                        Box(
                            modifier =
                                Modifier
                                    .width(itemWidth)
                                    .clickable {
                                        goToHistory(report.id)
                                    }
                        ) {
                            ReportCard(
                                report = report,
                                onPress = {
                                    goToHistory(report.id)
                                }
                            )
                        }
                    }
                }
            }
        } else {
            Column(
                modifier =
                    Modifier
                        .padding(horizontal = 2.dp)
                        .height(100.dp)
                        .fillMaxWidth()
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(10.dp)
                        ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
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
            Schedule(Schedule(), emptyList())
        }
    }
}

@Preview
@Composable
fun ScheduleMenuPreview() {
    RamenNoteTheme {
        ScheduleMenu(
            Modifier.background(Color.Gray),
            Schedule(
                shopId = 1,
                shopName = "一風堂 博多本店",
                isReported = false
            )
        )
    }
}

@Preview
@Composable
fun RecentReportsPreview() {
    RamenNoteTheme {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            RecentReports(
                reports = MockHomeViewModel().threeMonthsReports.value
            ) {}
        }
    }
}

@Preview
@Composable
fun ReportCardPreview() {
    RamenNoteTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            ReportCard(
                report =
                    FullReport(
                        id = 1,
                        shopId = 1,
                        shopName = "一風堂 博多本店",
                        menuName = "白丸元味",
                        photoName = "hakata_ramen_1.jpg",
                        imageBytes = null,
                        impression = "とんこつスープが濃厚で美味しかった。麺も硬めで好みの硬さだった。",
                        date = kotlinx.datetime.LocalDate.parse("2024-12-15"),
                        star = 1
                    ),
                onLongPress = { }
            )
        }
    }
}

@Preview
@Composable
fun FavoriteShopItemPreview() {
    RamenNoteTheme {
        Column(
            modifier = Modifier.width(130.dp).padding(all = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 短い店舗名（通常表示）
            FavoriteShopItem(
                shop =
                    Shop(
                        id = 1,
                        name = "一風堂",
                        area = "福岡県",
                        shopUrl = "https://www.ippudo.com/",
                        mapUrl = "",
                        star = 5,
                        stationName = "博多駅",
                        category = "とんこつラーメン",
                        favorite = true
                    ),
                imageBytes = null,
                onClick = {}
            )

            // 長い店舗名（...表示）
            FavoriteShopItem(
                shop =
                    Shop(
                        id = 2,
                        name = "博多一風堂本店 天神地下街店 とんこつラーメン専門店",
                        area = "福岡県",
                        shopUrl = "https://www.ippudo.com/",
                        mapUrl = "",
                        star = 5,
                        stationName = "天神駅",
                        category = "とんこつラーメン",
                        favorite = true
                    ),
                imageBytes = null,
                onClick = {}
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
                MockHomeViewModel().favoriteShops.value
            )
        }
    }
}
