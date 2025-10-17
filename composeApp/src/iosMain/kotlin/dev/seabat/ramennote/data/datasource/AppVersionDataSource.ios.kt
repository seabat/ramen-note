package dev.seabat.ramennote.data.datasource

import platform.Foundation.NSBundle
import platform.Foundation.NSBundleMainBundle

actual class AppVersionDataSource {
    
    actual fun getVersionName(): String {
        return NSBundleMainBundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as? String ?: "Unknown"
    }
    
    actual fun getVersionCode(): Int {
        return (NSBundleMainBundle.objectForInfoDictionaryKey("CFBundleVersion") as? String)?.toIntOrNull() ?: 0
    }
}
