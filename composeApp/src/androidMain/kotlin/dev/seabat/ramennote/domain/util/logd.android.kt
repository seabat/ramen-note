package dev.seabat.ramennote.domain.util

import android.util.Log
import dev.seabat.ramennote.BuildConfig

actual fun logd(tag: String, message: String) {
    if (BuildConfig.DEBUG) {
        Log.d(tag, message)
    }
}
