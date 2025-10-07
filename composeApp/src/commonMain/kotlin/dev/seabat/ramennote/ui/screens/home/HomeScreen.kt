package dev.seabat.ramennote.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.model.Shop
import dev.seabat.ramennote.ui.components.AppProgressBar
import dev.seabat.ramennote.ui.components.PlatformWebView
import dev.seabat.ramennote.ui.theme.RamenNoteTheme
import dev.seabat.ramennote.ui.util.createFormattedDateString
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModelContract = koinViewModel<HomeViewModel>()
) {
    val scheduledShop by viewModel.scheduledShop.collectAsStateWithLifecycle()
    val scheduledShopState by viewModel.scheduledShopState.collectAsStateWithLifecycle()
    
    LaunchedEffect(Unit) {
        viewModel.loadRecentSchedule()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            MainContent(scheduledShop)
        }

        ScheduledShopState(scheduledShopState) {
            viewModel.setScheduledShopStateToIdle()
        }
    }
}

@Composable
fun MainContent(scheduledShop: Shop?) {
    Schedule(scheduledShop)
}

@Composable
fun Schedule(scheduledShop: Shop?) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        // メインのBox
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(10.dp)
                ),
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
                        .padding(16.dp),
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

                    Row{}
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
fun MainContentPreview() {
    RamenNoteTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            MainContent(Shop())
        }
    }
}

