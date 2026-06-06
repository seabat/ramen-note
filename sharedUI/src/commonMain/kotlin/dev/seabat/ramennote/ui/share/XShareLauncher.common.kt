package dev.seabat.ramennote.ui.share

import androidx.compose.runtime.Composable
import dev.seabat.ramennote.ui.gallery.SharedImage

expect class XShareLauncher {
    fun shareToX(text: String, image: SharedImage?)
}

@Composable
expect fun createRememberedXShareLauncher(): XShareLauncher

fun createPostText(shopName: String, menuName: String, impression: String): String =
    buildString {
        append("$shopName")
        if (menuName.isNotBlank()) {
            append("\n$menuName")
        }
        if (impression.isNotBlank()) {
            append("\n$impression")
        }
    }
