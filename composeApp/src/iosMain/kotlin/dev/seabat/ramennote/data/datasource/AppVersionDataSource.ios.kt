package dev.seabat.ramennote.data.datasource

import platform.Foundation.NSBundle

actual class AppVersionDataSource {
    actual fun getVersionName(): String =
        NSBundle.mainBundle.objectForInfoDictionaryKey(
            "CFBundleShortVersionString"
        ) as? String ?: "Unknown"

    actual fun getVersionCode(): Int = (NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleVersion") as? String)?.toIntOrNull() ?: 0
}
