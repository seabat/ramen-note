package dev.seabat.ramennote.domain.util

import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.Platform

@OptIn(ExperimentalNativeApi::class)
actual fun logd(tag: String, message: String) {
    if (Platform.isDebugBinary) {
        println("$tag: $message")
    }
}
