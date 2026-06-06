package dev.seabat.ramennote.ui.gallery

import android.content.ContentResolver
import android.graphics.BitmapFactory
import android.net.Uri
import dev.seabat.ramennote.domain.util.logd
import java.io.InputStream

object BitmapUtils {
    fun getBitmapFromUri(uri: Uri, contentResolver: ContentResolver): android.graphics.Bitmap? {
        var inputStream: InputStream? = null
        try {
            inputStream = contentResolver.openInputStream(uri)
            val s = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            return s
        } catch (e: Exception) {
            e.printStackTrace()
            logd(message = "getBitmapFromUri Exception message(${e.message}")
            logd(message = "getBitmapFromUri Exception localizedMessage(${e.localizedMessage})")
            return null
        }
    }
}
