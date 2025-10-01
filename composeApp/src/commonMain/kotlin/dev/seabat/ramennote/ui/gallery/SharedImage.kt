package dev.seabat.ramennote.ui.gallery

import androidx.compose.ui.graphics.ImageBitmap

expect class SharedImage {
    constructor()
    fun toByteArray(): ByteArray?
    fun toImageBitmap(): ImageBitmap?
}