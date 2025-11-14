package dev.seabat.ramennote.ui.permission

import androidx.compose.runtime.Composable

expect class PermissionsLauncher(
    callback: PermissionCallback
) : PermissionHandler

interface PermissionCallback {
    fun onPermissionStatus(permissionType: PermissionType, status: PermissionStatus)
}

@Composable
expect fun createRememberedPermissionsLauncher(callback: PermissionCallback): PermissionsLauncher

interface PermissionHandler {
    @Composable
    fun askPermission(permission: PermissionType)

    @Composable
    fun isPermissionGranted(permission: PermissionType): Boolean
}

@Composable
expect fun launchSettings()
