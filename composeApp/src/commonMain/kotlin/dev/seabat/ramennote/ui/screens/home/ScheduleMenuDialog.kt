package dev.seabat.ramennote.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.seabat.ramennote.domain.model.Schedule
import dev.seabat.ramennote.ui.components.dialog.WideDialog
import dev.seabat.ramennote.ui.theme.RamenNoteTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import ramennote.composeapp.generated.resources.Res
import ramennote.composeapp.generated.resources.book_5_24px
import ramennote.composeapp.generated.resources.globe_24px
import ramennote.composeapp.generated.resources.home_menu_map
import ramennote.composeapp.generated.resources.home_menu_write_report
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
    WideDialog(onDismiss = onDismiss) {
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
                    text = stringResource(Res.string.home_menu_write_report),
                    onClick = onAddReport
                )
            }
        }
    }
}

@Preview
@Composable
fun ScheduleMenuDialogPreview() {
    RamenNoteTheme {
        ScheduleMenuDialog(
            schedule = Schedule(),
            onDismiss = {},
            onShowDetails = {},
            onShowWeb = {},
            onShowMap = {},
            onAddReport = {}
        )
    }
}
