package dev.seabat.ramennote.ui.screens.note.shop

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import ramennote.composeapp.generated.resources.Res
import ramennote.composeapp.generated.resources.add_no_data_label

@Composable
fun ShopDetailItem(
    label: String,
    value: String,
    enabledBorder: Boolean = false
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            modifier = Modifier.padding(end = 16.dp),
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )

        Text(
            modifier =
                Modifier
                    .then(
                        if (enabledBorder &&
                            (value.isNotEmpty() && value != stringResource(Res.string.add_no_data_label))
                        ) {
                            Modifier
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outline,
                                    shape = RoundedCornerShape(8.dp)
                                ).fillMaxWidth()
                        } else {
                            Modifier
                        }
                    ).padding(start = 8.dp, top = 4.dp, bottom = 4.dp),
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ShopDetailItemPreview() {
    MaterialTheme {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            ShopDetailItem(
                label = "店舗名",
                value = "ラーメン二郎 新宿店"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShopDetailItemEnabledBorder1Preview() {
    MaterialTheme {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            ShopDetailItem(
                label = "ノート",
                value =
                    "XXXXX",
                enabledBorder = true
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShopDetailItemEnabledBorder2Preview() {
    MaterialTheme {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            ShopDetailItem(
                label = "ノート",
                value =
                    "【機能】\n" +
                        "・訪店予定および訪店後のラーメン店をエリアごとにグループ化して管理\n" +
                        "・訪店予定および訪店後のラーメン店の Web サイト URL、Google マップ URL、評価、最寄り駅等の情報を登録\n" +
                        "・お気に入りラーメン店の登録\n" +
                        "・ラーメン店への訪店予定を登録\n" +
                        "・訪店後のラーメンの食レポを登録\n" +
                        "\n" +
                        "【使い方】\n" +
                        "[気になるラーメン店を登録] → [訪店予定を登録] → [訪店当日にマップ情報等を確認] → [訪店後に食レポを登録]",
                enabledBorder = true
            )
        }
    }
}
