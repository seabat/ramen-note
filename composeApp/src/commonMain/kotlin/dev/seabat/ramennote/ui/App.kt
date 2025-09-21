package dev.seabat.ramennote.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import dev.seabat.ramennote.ui.navigation.MainNavigation
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        MainNavigation()
    }
}