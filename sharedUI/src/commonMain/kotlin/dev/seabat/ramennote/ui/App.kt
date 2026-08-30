package dev.seabat.ramennote.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import dev.seabat.ramennote.ui.navigation.MainNavigation
import dev.seabat.ramennote.ui.theme.RamenNoteTheme

@Composable
@Preview
fun App() {
    RamenNoteTheme {
        MainNavigation()
    }
}
