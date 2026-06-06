package dev.seabat.ramennote.ui.screens.componens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.seabat.ramennote.domain.model.Shop
import dev.seabat.ramennote.ui.components.ShopStarIcon
import dev.seabat.ramennote.ui.theme.RamenNoteTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import ramennote.sharedui.generated.resources.Res
import ramennote.sharedui.generated.resources.favorite_disabled
import ramennote.sharedui.generated.resources.favorite_enabled

@Composable
fun ShopItem(
    shop: Shop,
    hasDivider: Boolean = true,
    onShopClick: () -> Unit,
    onDelete: () -> Unit
) {
    Column {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clickable { onShopClick() }
                    .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1.0f),
                text = shop.name,
                style = MaterialTheme.typography.titleMedium
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(3) { index ->
                        ShopStarIcon(
                            onOff = index < shop.star
                        )
                    }
                }

                // お気に入りアイコン
                Image(
                    painter =
                        painterResource(
                            if (shop.favorite) {
                                Res.drawable.favorite_enabled
                            } else {
                                Res.drawable.favorite_disabled
                            }
                        ),
                    contentDescription = if (shop.favorite) "お気に入り済み" else "お気に入り未設定",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        if (hasDivider) {
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )
        }
    }
}

@Preview
@Composable
fun ShopItemPreview() {
    RamenNoteTheme {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // お気に入りあり、評価3つ星
            ShopItem(
                shop =
                    Shop(
                        id = 1,
                        name = "一風堂 博多本店",
                        areaId = 1,
                        shopUrl = "https://www.ippudo.com/",
                        mapUrl = "",
                        star = 3,
                        stationName = "博多駅",
                        category = "豚骨",
                        favorite = true
                    ),
                onShopClick = {},
                onDelete = {}
            )

            // お気に入りなし、評価2つ星
            ShopItem(
                shop =
                    Shop(
                        id = 2,
                        name = "ラーメン横綱",
                        areaId = 2,
                        shopUrl = "",
                        mapUrl = "",
                        star = 2,
                        stationName = "新宿駅",
                        category = "醤油",
                        favorite = false
                    ),
                onShopClick = {},
                onDelete = {}
            )

            // お気に入りあり、評価1つ星
            ShopItem(
                shop =
                    Shop(
                        id = 3,
                        name = "つけ麺専門店 大勝軒 池袋店",
                        areaId = 2,
                        shopUrl = "",
                        mapUrl = "",
                        star = 1,
                        stationName = "池袋駅",
                        category = "つけ麺",
                        favorite = true
                    ),
                onShopClick = {},
                onDelete = {}
            )
        }
    }
}
