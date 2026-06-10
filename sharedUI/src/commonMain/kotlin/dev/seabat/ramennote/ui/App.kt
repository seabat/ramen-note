package dev.seabat.ramennote.ui

import androidx.compose.runtime.Composable
import dev.seabat.ramennote.ui.navigation.MainNavigation
import dev.seabat.ramennote.ui.theme.RamenNoteTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    RamenNoteTheme {
        MainNavigation()
    }
}
