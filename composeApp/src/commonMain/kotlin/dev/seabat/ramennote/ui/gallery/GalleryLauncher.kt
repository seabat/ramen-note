package dev.seabat.ramennote.ui.gallery

import androidx.compose.runtime.Composable

@Composable
expect fun createRememberedGalleryLauncher(onResult: (SharedImage?) -> Unit): GalleryLauncher


expect class GalleryLauncher(
    onLaunch: () -> Unit
) {
    fun launch()
}