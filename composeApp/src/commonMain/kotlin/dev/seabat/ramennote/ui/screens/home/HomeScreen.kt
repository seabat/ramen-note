package dev.seabat.ramennote.ui.screens.home

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import dev.seabat.ramennote.domain.extension.isTodayOrFuture
import dev.seabat.ramennote.domain.model.FullReport
import dev.seabat.ramennote.domain.model.MonthlyReportCount
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.model.Schedule
import dev.seabat.ramennote.domain.model.Shop
import dev.seabat.ramennote.domain.util.createTodayLocalDate
import dev.seabat.ramennote.ui.components.AppProgressBar
import dev.seabat.ramennote.ui.components.alert.AppAlert
import dev.seabat.ramennote.ui.components.chart.StackedBarChart
import dev.seabat.ramennote.ui.screens.componens.ReportCard
import dev.seabat.ramennote.ui.screens.componens.ShopItem
import dev.seabat.ramennote.ui.screens.history.ReportImageDialog
import dev.seabat.ramennote.ui.share.createRememberedXShareLauncher
import dev.seabat.ramennote.ui.theme.RamenNoteTheme
import dev.seabat.ramennote.ui.util.createFormattedDateString
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import ramennote.composeapp.generated.resources.Res
import ramennote.composeapp.generated.resources.add_schedule_error_past_date_message
import ramennote.composeapp.generated.resources.favorite_enabled
import ramennote.composeapp.generated.resources.home_background
import ramennote.composeapp.generated.resources.home_complete_add_schedule
import ramennote.composeapp.generated.resources.home_favorite_subheading
import ramennote.composeapp.generated.resources.home_monthly_report_count
import ramennote.composeapp.generated.resources.home_no_favorite
import ramennote.composeapp.generated.resources.home_no_map
import ramennote.composeapp.generated.resources.home_no_reports
import ramennote.composeapp.generated.resources.home_no_schedule
import ramennote.composeapp.generated.resources.home_no_web
import ramennote.composeapp.generated.resources.home_report_subheading
import ramennote.composeapp.generated.resources.home_schedule_date
import ramennote.composeapp.generated.resources.home_today_schedule
import ramennote.composeapp.generated.resources.home_tomorrow_schedule
import ramennote.composeapp.generated.resources.schedule_picker_label

private const val FAVORITE_SHOP_ITEM_HEIGHT = 66

private sealed interface DialogState {
    object Hidden : DialogState

    data class FavoriteShopMenu(
        val shop: Shop
    ) : DialogState

    data class ScheduleMenu(
        val schedule: Schedule
    ) : DialogState

    data class DatePicker(
        val shop: Shop
    ) : DialogState

    object PastDateAlert : DialogState

    object EmptyMapAlert : DialogState

    object EmptyWebAlert : DialogState

    object CompleteAddSchedule : DialogState
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    goToShop: (shopId: Int, shopName: String) -> Unit = { _, _ -> },
    goToReport: (shopId: Int, shopName: String, menuName: String, iso8601Date: String) -> Unit = { _, _, _, _ -> },
    goToHistory: (reportId: Int) -> Unit = { _ -> },
    goToHistoryWithFiltering: (shopId: Int) -> Unit = { _ -> },
    viewModel: HomeViewModelContract = koinViewModel<HomeViewModel>()
) {
    val schedule by viewModel.schedule.collectAsStateWithLifecycle()
    val loadedScheduleState by viewModel.loadedScheduleState.collectAsStateWithLifecycle()
    val addedScheduleState by viewModel.addedScheduleState.collectAsStateWithLifecycle()
    val favoriteShops by viewModel.favoriteShops.collectAsStateWithLifecycle()
    val recentReports by viewModel.recentReports.collectAsStateWithLifecycle()
    val yearlyReportStats by viewModel.yearlyReportStats.collectAsStateWithLifecycle()
    var dialogState by remember { mutableStateOf<DialogState>(DialogState.Hidden) }
    var selectedImageBytes by remember { mutableStateOf<ByteArray?>(null) }
    val urlHandler = LocalUriHandler.current
    val xShareLauncher = createRememberedXShareLauncher()

    LaunchedEffect(Unit) {
        viewModel.loadRecentSchedule()
        viewModel.loadFavoriteShops()
        viewModel.loadRecentReports()
        viewModel.loadYearlyReportStats()
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
                    .verticalScroll(rememberScrollState())
                    .padding(top = 4.dp, bottom = 4.dp, start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            ScheduleContent(
                schedule,
                showScheduleMenu = {
                    dialogState = DialogState.ScheduleMenu(it)
                }
            )

            Spacer(modifier = Modifier.height(6.dp))

            // 過去1年間の積み上げグラフ
            YearlyReportChart(yearlyReportStats)

            Spacer(modifier = Modifier.height(6.dp))

            // 最新10件のレポートを水平ページャーで表示
            RecentReports(
                reports = recentReports,
                goToHistory = goToHistory,
                onImageTap = { imageBytes -> selectedImageBytes = imageBytes },
                shareToX = { postText, photoName ->
                    viewModel.shareToX(postText, photoName, xShareLauncher)
                }
            )

            Spacer(modifier = Modifier.height(6.dp))

            Favorite(
                favoriteShops,
                onShopClick = { shop ->
                    dialogState = DialogState.FavoriteShopMenu(shop)
                }
            )
        }

        ScheduledShopState(loadedScheduleState) {
            viewModel.setLoadedScheduleStateToIdle()
        }

        AddScheduleState(addedScheduleState) {
            dialogState = DialogState.CompleteAddSchedule
            viewModel.setAddedScheduleStateToIdle()
        }

        HomeDialog(
            dialogState,
            onDismiss = { dialogState = DialogState.Hidden },
            goToShop = { shopId, shopName ->
                goToShop(shopId, shopName)
                dialogState = DialogState.Hidden
            },
            goToReport = { shopId, shopName, menuName, iso8601Date ->
                goToReport(shopId, shopName, menuName, iso8601Date)
                dialogState = DialogState.Hidden
            },
            goToHistoryWithFiltering = { shopId ->
                goToHistoryWithFiltering(shopId)
                dialogState = DialogState.Hidden
            },
            showDatePicker = { shop ->
                dialogState = DialogState.DatePicker(shop)
            },
            launchMap = { mapUrl ->
                urlHandler.openUri(mapUrl)
                dialogState = DialogState.Hidden
            },
            showEmptyMapError = {
                dialogState = DialogState.EmptyMapAlert
            },
            showEmptyWebError = {
                dialogState = DialogState.EmptyWebAlert
            },
            showPastDateError = {
                dialogState = DialogState.PastDateAlert
            },
            addSchedule = { shopId, date ->
                viewModel.addSchedule(shopId, date)
                dialogState = DialogState.Hidden
            },
            reloadSchedule = {
                viewModel.loadRecentSchedule()
                dialogState = DialogState.Hidden
            }
        )

        // 画像ダイアログ
        selectedImageBytes?.let { imageBytes ->
            ReportImageDialog(
                model = imageBytes,
                onDismiss = { selectedImageBytes = null }
            )
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
private fun ScheduleContent(
    schedule: Schedule?,
    showScheduleMenu: (schedule: Schedule) -> Unit
) {
    // 今日の予定かどうかを判定
    val isToday = schedule?.scheduledDate == createTodayLocalDate()

    // アニメーション開始フラグ（HomeScreen表示時に一度だけアニメーションを実行）
    var animationTrigger by remember { mutableStateOf(false) }

    LaunchedEffect(isToday) {
        if (isToday) {
            animationTrigger = true
            // アニメーション時間を計算（最も長いアニメーションに合わせる）
            // グロー: 5回 × 1000ms = 5000ms
            // シャイン: 3回 × 2500ms = 7500ms
            // 最大7500ms + 少し余裕を持たせて8000ms
            kotlinx.coroutines.delay(8000)
            animationTrigger = false
            // アニメーション終了後に初期値に戻るための少しの遅延
            kotlinx.coroutines.delay(300)
        } else {
            animationTrigger = false
        }
    }

    // アニメーション用のTransition（常に作成）
    val infiniteTransition = rememberInfiniteTransition(label = "schedule_animation")

    // グローアニメーション（ボーダーの明るさ）- より目立つように範囲を拡大
    // アニメーション中は0.4fから1.0fの間で変化、終了後は1.0f（通常のボーダー色）に戻る
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = if (isToday && animationTrigger) 1.0f else 0.4f,
        animationSpec =
            infiniteRepeatable(
                animation =
                    if (isToday && animationTrigger) {
                        tween(1000, easing = LinearEasing)
                    } else {
                        tween(1) // アニメーションしない（最小時間）
                    },
                repeatMode = RepeatMode.Reverse
            ),
        label = "glow_alpha"
    )

    // グローアニメーション用の実際のalpha値（0.4fから1.0fの間で変化）
    val actualGlowAlpha =
        if (isToday && animationTrigger) {
            glowAlpha // 0.4fから1.0fの間で変化
        } else {
            1.0f // 通常時は通常のボーダー色
        }

    // 今日の予定用の目立つグローカラー（濃いシアン）
    val glowColor = Color(0xFF008B8D) // 濃いシアン（DarkCyan）

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text =
                schedule?.scheduledDate?.let { scheduledDate ->
                    val today = createTodayLocalDate()
                    val tomorrow = today.plus(DatePeriod(days = 1))
                    when (scheduledDate) {
                        today -> stringResource(Res.string.home_today_schedule)
                        tomorrow -> stringResource(Res.string.home_tomorrow_schedule)
                        else -> stringResource(Res.string.home_schedule_date, createFormattedDateString(scheduledDate))
                    }
                } ?: stringResource(Res.string.home_no_schedule),
            style = MaterialTheme.typography.titleMedium,
            color =
                if (isToday) {
                    // グローアニメーションを適用
                    glowColor.copy(alpha = actualGlowAlpha)
                } else {
                    MaterialTheme.colorScheme.secondary
                }
        )

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .border(
                        width = if (isToday) 3.dp else 2.dp,
                        color =
                            if (isToday) {
                                // グローアニメーション用の目立つ色を使用
                                glowColor.copy(alpha = actualGlowAlpha)
                            } else {
                                MaterialTheme.colorScheme.outline
                            },
                        shape = RoundedCornerShape(6.dp)
                    ).clip(RoundedCornerShape(6.dp))
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
            ) {
                // メニュー
                if (schedule != null) {
                    ShopItem(
                        shop =
                            Shop(
                                id = schedule.shopId,
                                name = schedule.shopName,
                                shopUrl = schedule.shopUrl,
                                mapUrl = schedule.mapUrl,
                                star = schedule.star,
                                category = schedule.category,
                                scheduledDate = schedule.scheduledDate,
                                menuName1 = schedule.menuName,
                                photoName1 = schedule.photoName,
                                favorite = schedule.favorite
                            ),
                        hasDivider = false,
                        onShopClick = {
                            showScheduleMenu(schedule)
                        },
                        onDelete = {}
                    )
                } else {
                    // ShopItemと同じ高さを維持するための空のColumn
                    Column {
                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .height(24.dp)
                            // titleMediumの高さに合わせる
                        ) {
                            // 空のスペース
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Favorite(
    favoriteShops: List<ShopWithImage>,
    onShopClick: (Shop) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // タイトル
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
                // FavoriteShopItem を縦に3個分表示できる高さ
                val gridHeight =
                    FAVORITE_SHOP_ITEM_HEIGHT.dp * 3 +
                        8.dp * 4 // contentPadding(上下) + item間スペース(2つ) 分

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier =
                        Modifier
                            .padding(4.dp)
                            .height(gridHeight),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(favoriteShops) { shopWithImage ->
                        FavoriteShopItem(
                            shop = shopWithImage.shop,
                            imagePath = shopWithImage.imagePath,
                            onClick = { onShopClick(shopWithImage.shop) }
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
    imagePath: String?,
    onClick: () -> Unit
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(FAVORITE_SHOP_ITEM_HEIGHT.dp),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background
            )
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.surfaceContainerLow),
            contentAlignment = Alignment.Center
        ) {
            // 背景画像（半透明）
            if (imagePath != null) {
                AsyncImage(
                    model = imagePath,
                    contentDescription = null,
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp)),
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
                                        Color.Black.copy(alpha = 0.80f)
                                    ),
                                startY = 0f,
                                endY = 200f
                            )
                        )
            )

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 店舗名テキスト
                Text(
                    text = shop.name,
                    style =
                        MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.White
                )
            }

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
}

@Composable
private fun ScheduledShopState(
    state: RunStatus<Schedule?>,
    setIdle: () -> Unit
) {
    when (state) {
        is RunStatus.Success -> {
            setIdle()
        }
        is RunStatus.Error -> { /* Do nothing */ }
        is RunStatus.Loading -> {
            AppProgressBar()
        }
        is RunStatus.Idle -> { /* Do nothing */ }
    }
}

@Composable
private fun AddScheduleState(
    state: RunStatus<String>,
    onCompleteAddSchedule: () -> Unit
) {
    when (state) {
        is RunStatus.Success -> {
            onCompleteAddSchedule()
        }
        is RunStatus.Error -> { /* Do nothing */ }
        is RunStatus.Loading -> {
            AppProgressBar()
        }
        is RunStatus.Idle -> { /* Do nothing */ }
    }
}

@Composable
private fun YearlyReportChart(
    monthlyData: List<MonthlyReportCount>
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(Res.string.home_monthly_report_count),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.secondary
        )
        StackedBarChart(monthlyData = monthlyData)
    }
}

@Composable
private fun RecentReports(
    reports: List<FullReport>,
    goToHistory: (reportId: Int) -> Unit,
    onImageTap: (ByteArray?) -> Unit,
    shareToX: (String, String) -> Unit
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
                                onTap = {
                                    goToHistory(report.id)
                                },
                                onImageTap = { onImageTap(null) },
                                onShareTap = { postText, photoName ->
                                    shareToX(postText, photoName)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeDialog(
    dialogState: DialogState,
    onDismiss: () -> Unit,
    goToShop: (shopId: Int, shopName: String) -> Unit,
    goToReport: (shopId: Int, shopName: String, menuName: String, iso8601Date: String) -> Unit,
    goToHistoryWithFiltering: (shopId: Int) -> Unit = { _ -> },
    showDatePicker: (shop: Shop) -> Unit,
    launchMap: (mapUrl: String) -> Unit,
    addSchedule: (shopId: Int, date: LocalDate) -> Unit,
    showEmptyMapError: () -> Unit,
    showEmptyWebError: () -> Unit,
    showPastDateError: () -> Unit,
    reloadSchedule: () -> Unit
) {
    when (dialogState) {
        is DialogState.Hidden -> {}
        is DialogState.FavoriteShopMenu -> {
            FavoriteShopMenuDialog(
                onDismiss = { onDismiss() },
                onShowDetails = {
                    goToShop(dialogState.shop.id, dialogState.shop.name)
                    onDismiss()
                },
                onShowMap = {
                    if (dialogState.shop.mapUrl.isNotEmpty()) {
                        launchMap(dialogState.shop.mapUrl)
                    } else {
                        showEmptyMapError()
                    }
                },
                onAddReport = {
                    goToReport(
                        dialogState.shop.id,
                        dialogState.shop.name,
                        "",
                        dialogState.shop.scheduledDate?.toString() ?: createTodayLocalDate().toString()
                    )
                    onDismiss()
                },
                onAddSchedule = {
                    showDatePicker(dialogState.shop)
                },
                onShowReport = {
                    goToHistoryWithFiltering(dialogState.shop.id)
                }
            )
        }
        is DialogState.ScheduleMenu -> {
            ScheduleMenuDialog(
                schedule = dialogState.schedule,
                onDismiss = { onDismiss() },
                onShowDetails = {
                    goToShop(dialogState.schedule.shopId, dialogState.schedule.shopName)
                    onDismiss()
                },
                onShowWeb = {
                    if (dialogState.schedule.shopUrl.isNotEmpty()) {
                        launchMap(dialogState.schedule.shopUrl)
                    } else {
                        showEmptyWebError()
                    }
                },
                onShowMap = {
                    if (dialogState.schedule.mapUrl.isNotEmpty()) {
                        launchMap(dialogState.schedule.mapUrl)
                    } else {
                        showEmptyMapError()
                    }
                },
                onAddReport = {
                    goToReport(
                        dialogState.schedule.shopId,
                        dialogState.schedule.shopName,
                        "",
                        dialogState.schedule.scheduledDate?.toString() ?: createTodayLocalDate().toString()
                    )
                    onDismiss()
                }
            )
        }
        is DialogState.DatePicker -> {
            // メニューダイアログと日付ピッカーの状態管理
            val datePickerState = rememberDatePickerState()
            DatePickerDialog(
                onDismissRequest = { onDismiss() },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val millis = datePickerState.selectedDateMillis
                            if (millis != null) {
                                val selectedDate =
                                    Instant
                                        .fromEpochMilliseconds(millis)
                                        .toLocalDateTime(TimeZone.currentSystemDefault())
                                        .date

                                // 今日を含めた未来日かどうかをチェック
                                if (!selectedDate.isTodayOrFuture()) {
                                    showPastDateError()
                                } else {
                                    addSchedule(dialogState.shop.id, selectedDate)
                                }
                            }
                            onDismiss()
                        }
                    ) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { onDismiss() }) { Text("Cancel") }
                }
            ) {
                Column {
                    Text(
                        text = stringResource(Res.string.schedule_picker_label),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    DatePicker(state = datePickerState)
                }
            }
        }
        is DialogState.PastDateAlert -> {
            AppAlert(
                message = stringResource(Res.string.add_schedule_error_past_date_message),
                onConfirm = { onDismiss() }
            )
        }
        is DialogState.EmptyMapAlert -> {
            AppAlert(
                message = stringResource(Res.string.home_no_map),
                onConfirm = { onDismiss() }
            )
        }
        is DialogState.EmptyWebAlert -> {
            AppAlert(
                message = stringResource(Res.string.home_no_web),
                onConfirm = { onDismiss() }
            )
        }
        is DialogState.CompleteAddSchedule -> {
            AppAlert(
                message = stringResource(Res.string.home_complete_add_schedule),
                onConfirm = { reloadSchedule() }
            )
        }
    }
}

@Preview
@Composable
fun ScheduleContentPreview() {
    RamenNoteTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            ScheduleContent(Schedule(), {})
        }
    }
}

@Preview
@Composable
fun RecentReportsPreview() {
    RamenNoteTheme {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            RecentReports(
                reports = MockHomeViewModel().recentReports.value,
                {},
                { _ -> },
                { _, _ -> }
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
                report =
                    FullReport(
                        id = 1,
                        shopId = 1,
                        shopName = "一風堂 博多本店",
                        menuName = "白丸元味",
                        photoName = "hakata_ramen_1.jpg",
                        imageBytes = null,
                        impression = "とんこつスープが濃厚で美味しかった。麺も硬めで好みの硬さだった。",
                        date = LocalDate.parse("2024-12-15"),
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
                        areaId = 1,
                        shopUrl = "https://www.ippudo.com/",
                        mapUrl = "",
                        star = 5,
                        stationName = "博多駅",
                        category = "とんこつラーメン",
                        favorite = true
                    ),
                imagePath = null,
                onClick = {}
            )

            // 長い店舗名（...表示）
            FavoriteShopItem(
                shop =
                    Shop(
                        id = 2,
                        name = "博多一風堂本店 天神地下街店 とんこつラーメン専門店",
                        areaId = 1,
                        shopUrl = "https://www.ippudo.com/",
                        mapUrl = "",
                        star = 5,
                        stationName = "天神駅",
                        category = "とんこつラーメン",
                        favorite = true
                    ),
                imagePath = null,
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
                MockHomeViewModel().favoriteShops.value,
                {}
            )
        }
    }
}

@Preview
@Composable
fun YearlyReportChartPreview() {
    RamenNoteTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            // 過去12ヶ月分のモックデータ
            val mockData =
                listOf(
                    MonthlyReportCount("2024-01", 3),
                    MonthlyReportCount("2024-02", 5),
                    MonthlyReportCount("2024-03", 0),
                    MonthlyReportCount("2024-04", 8),
                    MonthlyReportCount("2024-05", 4),
                    MonthlyReportCount("2024-06", 6),
                    MonthlyReportCount("2024-07", 10),
                    MonthlyReportCount("2024-08", 7),
                    MonthlyReportCount("2024-09", 5),
                    MonthlyReportCount("2024-10", 9),
                    MonthlyReportCount("2024-11", 6),
                    MonthlyReportCount("2024-12", 4)
                )
            YearlyReportChart(mockData)
        }
    }
}

@Preview
@Composable
fun YearlyReportChartEmptyPreview() {
    RamenNoteTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            // データがない場合のプレビュー
            YearlyReportChart(emptyList())
        }
    }
}
