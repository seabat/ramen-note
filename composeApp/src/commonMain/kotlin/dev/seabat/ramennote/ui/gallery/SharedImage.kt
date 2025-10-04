package dev.seabat.ramennote.ui.gallery

import androidx.compose.ui.graphics.ImageBitmap

expect class SharedImage {
    constructor()
    constructor(byteArray: ByteArray)
    fun toByteArray(): ByteArray?
    fun toImageBitmap(): ImageBitmap?
}