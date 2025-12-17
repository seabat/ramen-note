package dev.seabat.ramennote.ui.share

import androidx.compose.runtime.Composable
import dev.seabat.ramennote.ui.gallery.SharedImage

expect class XShareLauncher {
    fun shareToX(text: String, image: SharedImage?)
}

@Composable
expect fun createRememberedXShareLauncher(): XShareLauncher

