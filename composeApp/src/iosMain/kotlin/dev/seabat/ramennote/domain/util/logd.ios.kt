package dev.seabat.ramennote.domain.util

actual fun logd(tag: String, message: String) {
    println("$tag: $message")
}