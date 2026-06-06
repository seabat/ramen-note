package dev.seabat.ramennote.domain.util

import android.util.Log

actual fun logd(tag: String, message: String) {
    Log.d(tag, message)
}
