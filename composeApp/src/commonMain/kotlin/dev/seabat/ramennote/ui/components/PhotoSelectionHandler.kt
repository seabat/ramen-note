package dev.seabat.ramennote.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.seabat.ramennote.ui.gallery.SharedImage
import dev.seabat.ramennote.ui.gallery.createRememberedGalleryLauncher
import dev.seabat.ramennote.ui.permission.launchSettings

@Composable
fun PhotoSelectionHandler(
    onImageSelected: (SharedImage) -> Unit,
    permissionEnabled: Boolean,
    onPermissionEnabledChange: (Boolean) -> Unit
) {
    var shouldShowPermissionRationalDialog by remember { mutableStateOf(false) }
    var shouldLaunchSetting by remember { mutableStateOf(false) }
    val galleryManager = createRememberedGalleryLauncher { onImageSelected(it!!) }

    if (permissionEnabled) {
        Permission(
            permissionEnabled = {
                onPermissionEnabledChange(true)
            },
            showPermissionRationalDialog = {
                shouldShowPermissionRationalDialog = true
            },
            showGallery = {
                galleryManager.launch()
            }
        )
        onPermissionEnabledChange(false)
    }

    if (shouldShowPermissionRationalDialog) {
        AppTwoButtonAlert(
            message = "写真を選択するには、ストレージのアクセス許可が必要です。設定から許可してください。",
            confirmButtonText = "Settings",
            nagativeButtonText = "Cancel",
            onConfirm = {
                shouldShowPermissionRationalDialog = false
                shouldLaunchSetting = true
            },
            onNegative = {
                shouldShowPermissionRationalDialog = false
            }
        )
    }

    if (shouldLaunchSetting) {
        launchSettings()
        shouldLaunchSetting = false
    }
}