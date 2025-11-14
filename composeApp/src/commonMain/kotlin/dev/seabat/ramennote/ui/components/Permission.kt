package dev.seabat.ramennote.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import dev.seabat.ramennote.domain.util.logd
import dev.seabat.ramennote.ui.permission.PermissionCallback
import dev.seabat.ramennote.ui.permission.PermissionStatus
import dev.seabat.ramennote.ui.permission.PermissionType
import dev.seabat.ramennote.ui.permission.createRememberedPermissionsLauncher

@Composable
fun Permission(
    permissionEnabled: () -> Unit,
    showPermissionRationalDialog: () -> Unit,
    showGallery: () -> Unit
) {
    val currentCallback by rememberUpdatedState(
        object : PermissionCallback {
            override fun onPermissionStatus(
                permissionType: PermissionType,
                status: PermissionStatus
            ) {
                when (status) {
                    PermissionStatus.GRANTED -> {
                        when (permissionType) {
                            PermissionType.CAMERA -> { /* カメラ起動の処理 */ }
                            PermissionType.GALLERY -> {
                                permissionEnabled()
                            }
                        }
                    }
                    else -> {
                        showPermissionRationalDialog()
                    }
                }
            }
        }
    )
    val permissionLauncher = createRememberedPermissionsLauncher(currentCallback)
    val isGranted = permissionLauncher.isPermissionGranted(PermissionType.GALLERY)
    if (isGranted) {
        logd(message = "GALLERY Permission(granted)")
        showGallery()
    } else {
        logd(message = "GALLERY Permission(not Granted)")
        permissionLauncher.askPermission(PermissionType.GALLERY)
    }
}
