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
import dev.seabat.ramennote.ui.components.dialog.WideDialog
import dev.seabat.ramennote.ui.theme.RamenNoteTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import ramennote.composeapp.generated.resources.Res
import ramennote.composeapp.generated.resources.book_5_24px
import ramennote.composeapp.generated.resources.event_note_24px
import ramennote.composeapp.generated.resources.home_menu_map
import ramennote.composeapp.generated.resources.home_menu_report
import ramennote.composeapp.generated.resources.home_menu_write_report
import ramennote.composeapp.generated.resources.home_menu_schedule
import ramennote.composeapp.generated.resources.home_menu_shop_detail
import ramennote.composeapp.generated.resources.location_on_24px
import ramennote.composeapp.generated.resources.ramen_dining_24px

@Composable
fun FavoriteShopMenuDialog(
    onDismiss: () -> Unit,
    onShowDetails: () -> Unit,
    onShowMap: () -> Unit,
    onAddReport: () -> Unit,
    onAddSchedule: () -> Unit,
    onShowReport: () -> Unit,
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
            MenuItem(
                icon = vectorResource(Res.drawable.location_on_24px),
                text = stringResource(Res.string.home_menu_map),
                onClick = onShowMap
            )
            Spacer(modifier = Modifier.height(8.dp))
            MenuItem(
                icon = vectorResource(Res.drawable.event_note_24px),
                text = stringResource(Res.string.home_menu_schedule),
                onClick = onAddSchedule
            )
            Spacer(modifier = Modifier.height(8.dp))
            MenuItem(
                icon = vectorResource(Res.drawable.ramen_dining_24px),
                text = stringResource(Res.string.home_menu_report),
                onClick = onShowReport
            )
            Spacer(modifier = Modifier.height(8.dp))
            MenuItem(
                icon = vectorResource(Res.drawable.ramen_dining_24px),
                text = stringResource(Res.string.home_menu_write_report),
                onClick = onAddReport
            )
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
            onAddSchedule = {},
            onShowReport = {},
        )
    }
}
