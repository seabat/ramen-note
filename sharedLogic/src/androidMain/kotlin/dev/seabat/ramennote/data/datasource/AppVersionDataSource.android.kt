package dev.seabat.ramennote.data.datasource

import android.content.Context
import android.content.pm.PackageManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual class AppVersionDataSource : KoinComponent {
    private val context: Context by inject()

    actual fun getVersionName(): String =
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "Unknown"
        } catch (e: PackageManager.NameNotFoundException) {
            "Unknown"
        }

    actual fun getVersionCode(): Int =
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            0
        }
}
