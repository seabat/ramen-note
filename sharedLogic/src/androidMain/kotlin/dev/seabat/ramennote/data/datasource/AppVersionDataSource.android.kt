package dev.seabat.ramennote.data.datasource

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
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
            // versionCode は API 28 で非推奨。longVersionCode の下位 32 ビットが
            // 従来の versionCode と同値のため、toInt() で従来挙動を維持する。
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode.toInt()
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode
            }
        } catch (e: PackageManager.NameNotFoundException) {
            0
        }
}
