package dev.seabat.ramennote.ui.screens.note.shop

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import dev.seabat.ramennote.domain.model.Shop
import dev.seabat.ramennote.ui.component.AppBar
import dev.seabat.ramennote.ui.theme.RamenNoteTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import ramennote.composeapp.generated.resources.Res
import ramennote.composeapp.generated.resources.add_calender_button
import ramennote.composeapp.generated.resources.add_category_label
import ramennote.composeapp.generated.resources.add_edit_button
import ramennote.composeapp.generated.resources.add_evaluation_label
import ramennote.composeapp.generated.resources.add_map_label
import ramennote.composeapp.generated.resources.add_station_label
import ramennote.composeapp.generated.resources.add_web_site_label
import ramennote.composeapp.generated.resources.kid_star_24px_empty
import ramennote.composeapp.generated.resources.kid_star_24px_fill
import coil3.compose.AsyncImage
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ShopScreen(
    shop: Shop,
    onBackClick: () -> Unit,
    viewModel: ShopViewModelContract = koinViewModel<ShopViewModel>()
) {
    Scaffold(
        topBar = {
            AppBar(
                title = shop.name,
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // ヘッダー画像エリア
                LaunchedEffect(shop.name) {
                    viewModel.loadImage(shop)
                }
                val imageBytes by viewModel.shopImage.collectAsState()
                Header(imageBytes)

                // アクションボタン
                ActionButtons()

                // 詳細情報
                Detail(shop)
            }
        }
    }
}

@Composable
fun Header(imageBytes: ByteArray?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(color = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
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
                    )
                )
        )

        // 左右の矢印（スワイプ可能を示唆）
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "前へ",
                tint = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.padding(vertical = 16.dp)
            )
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "次へ",
                tint = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }
    }
}

// アクションボタン
@Composable
fun ActionButtons() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(
            onClick = { /* 予定追加処理 */ },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
            )
        ) {
            Text(stringResource(Res.string.add_calender_button), style = MaterialTheme.typography.titleMedium)
        }

        Button(
            onClick = { /* 編集処理 */ },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
            )
        ) {
            Text(stringResource(Res.string.add_edit_button), style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun Detail(shop: Shop) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {

        // Webサイト
        UrlItem(
            label = stringResource(Res.string.add_web_site_label),
            url = shop.shopUrl
        )

        // 地図
        UrlItem(
            label = stringResource(Res.string.add_map_label),
            url = shop.mapUrl
        )

        // 評価（星）
        Row(
            modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(Res.string.add_evaluation_label),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(end = 16.dp)
            )
            // 星の表示（最大3つ）
            Row {
                repeat(3) { index ->
                    Icon(
                        imageVector = if (index < shop.star) {
                            vectorResource(Res.drawable.kid_star_24px_fill)
                        } else {
                            vectorResource(Res.drawable.kid_star_24px_empty)
                        },
                        contentDescription = "星",
                        tint = if (index < shop.star) Color(0xFFFFEA00) else Color.White,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                }
            }
        }

        // 最寄り駅
        ShopDetailItem(
            label = stringResource(Res.string.add_station_label),
            value = shop.stationName,
            isClickable = false
        )

        // 系統
        ShopDetailItem(
            label = stringResource(Res.string.add_category_label),
            value = shop.category,
            isClickable = false
        )
    }
}

@Composable
private fun UrlItem(
    label: String,
    url: String,
) {
    val urlHandler = LocalUriHandler.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .clickable { urlHandler.openUri(url) },
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Blue
        )
    }
}

@Composable
private fun ShopDetailItem(
    label: String,
    value: String,
    isClickable: Boolean,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .then(
                if (isClickable && onClick != null) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isClickable) Color.Blue else Color.Black
        )

        if (isClickable) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Blue
            )
        } else {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview
@Composable
fun ShopScreenPreview() {
    RamenNoteTheme {
        ShopScreen(
            shop = Shop(
                name = "XXXX家",
                area = "東京",
                shopUrl = "https://example.com",
                mapUrl = "https://maps.google.com",
                star = 2,
                stationName = "JR渋谷駅",
                category = "家系"
            ),
            onBackClick = { },
        )
    }
}
