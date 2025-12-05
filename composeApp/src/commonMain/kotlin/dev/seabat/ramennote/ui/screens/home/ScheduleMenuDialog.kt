package dev.seabat.ramennote.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.seabat.ramennote.domain.model.Schedule
import dev.seabat.ramennote.ui.theme.RamenNoteTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import ramennote.composeapp.generated.resources.Res
import ramennote.composeapp.generated.resources.book_5_24px
import ramennote.composeapp.generated.resources.globe_24px
import ramennote.composeapp.generated.resources.home_menu_map
import ramennote.composeapp.generated.resources.home_menu_report
import ramennote.composeapp.generated.resources.home_menu_shop_detail
import ramennote.composeapp.generated.resources.home_menu_web
import ramennote.composeapp.generated.resources.location_on_24px
import ramennote.composeapp.generated.resources.ramen_dining_24px

@Composable
fun ScheduleMenuDialog(
    schedule: Schedule,
    onDismiss: () -> Unit,
    onShowDetails: () -> Unit,
    onShowWeb: () -> Unit,
    onShowMap: () -> Unit,
    onAddReport: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clickable { onDismiss() }
                    .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            // コンテンツ用のBox
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MenuItem(
                        icon = vectorResource(Res.drawable.book_5_24px),
                        text = stringResource(Res.string.home_menu_shop_detail),
                        onClick = onShowDetails
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Web サイト
                    MenuItem(
                        icon = vectorResource(Res.drawable.globe_24px),
                        text = stringResource(Res.string.home_menu_web),
                        onClick = onShowWeb
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // 地図
                    MenuItem(
                        icon = vectorResource(Res.drawable.location_on_24px),
                        text = stringResource(Res.string.home_menu_map),
                        onClick = onShowMap
                    )

                    if (!schedule.isReported) {
                        Spacer(modifier = Modifier.height(8.dp))
                        MenuItem(
                            icon = vectorResource(Res.drawable.ramen_dining_24px),
                            text = stringResource(Res.string.home_menu_report),
                            onClick = onAddReport
                        )
                    }
                }
            }

            // コンテンツBoxの右上角に配置
            // 親Boxの右上角から、padding分(32dp)左に移動するとコンテンツBoxの右端
            // コンテンツBoxの右上角に配置し、16dp外側（右方向）に突き出すには、-32 + 16 = -16dp
            // 閉じるボタンが左にずれているので、右方向に移動する（負の値を小さくする）
            Box(
                modifier =
                    Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 16.dp, y = (-16).dp) // コンテンツBoxの右上角に配置（右方向に調整）
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.background)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = CircleShape
                        ).clickable { onDismiss() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "閉じる",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Preview
@Composable
private fun FavoriteShopMenuDialogPreview() {
    RamenNoteTheme {
        FavoriteShopMenuDialog(
            onDismiss = {},
            onShowMap = {},
            onShowDetails = {},
            onAddReport = {},
            onAddSchedule = {}
        )
    }
}
