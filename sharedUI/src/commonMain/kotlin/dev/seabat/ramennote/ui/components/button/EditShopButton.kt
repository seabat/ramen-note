package dev.seabat.ramennote.ui.components.button

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.seabat.ramennote.ui.theme.RamenNoteTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import ramennote.sharedui.generated.resources.Res
import ramennote.sharedui.generated.resources.edit_24px
import ramennote.sharedui.generated.resources.shop_menu_edit_button

@Composable
fun EditShopButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ActionButton(
        icon = vectorResource(Res.drawable.edit_24px),
        text = stringResource(Res.string.shop_menu_edit_button),
        onClick = onClick,
        modifier = modifier
    )
}

@Preview
@Composable
private fun EditShopButtonPreview() {
    RamenNoteTheme {
        EditShopButton(onClick = {})
    }
}
