package dev.seabat.ramennote.ui.gallery

import android.content.ContentResolver
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

actual class GalleryLauncher actual constructor(private val onLaunch: () -> Unit) {
    actual fun launch() {
        onLaunch()
    }
}

@Composable
actual fun createRememberedGalleryLauncher(onResult: (SharedImage?) -> Unit): GalleryLauncher {
    val context = LocalContext.current
    val contentResolver: ContentResolver = context.contentResolver
    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let {
                onResult.invoke(SharedImage(BitmapUtils.getBitmapFromUri(uri, contentResolver)))
            }
        }
    return remember {
        GalleryLauncher(onLaunch = {
            galleryLauncher.launch(
                PickVisualMediaRequest(
                    mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        })
    }
}