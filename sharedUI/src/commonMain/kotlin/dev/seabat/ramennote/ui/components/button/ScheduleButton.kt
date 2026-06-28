package dev.seabat.ramennote.ui.components.button

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.seabat.ramennote.ui.theme.RamenNoteTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import ramennote.sharedui.generated.resources.Res
import ramennote.sharedui.generated.resources.event_note_24px
import ramennote.sharedui.generated.resources.shop_menu_schedule_add_button
import ramennote.sharedui.generated.resources.shop_menu_schedule_edit_button

@Composable
fun ScheduleButton(
    hasScheduledDate: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonText =
        if (hasScheduledDate) {
            stringResource(Res.string.shop_menu_schedule_edit_button)
        } else {
            stringResource(Res.string.shop_menu_schedule_add_button)
        }
    ActionButton(
        icon = vectorResource(Res.drawable.event_note_24px),
        text = buttonText,
        onClick = onClick,
        modifier = modifier
    )
}

@Preview
@Composable
private fun ScheduleButtonAddPreview() {
    RamenNoteTheme {
        ScheduleButton(hasScheduledDate = false, onClick = {})
    }
}

@Preview
@Composable
private fun ScheduleButtonEditPreview() {
    RamenNoteTheme {
        ScheduleButton(hasScheduledDate = true, onClick = {})
    }
}
