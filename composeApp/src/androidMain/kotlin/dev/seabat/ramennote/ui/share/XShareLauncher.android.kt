package dev.seabat.ramennote.ui.share

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import dev.seabat.ramennote.ui.gallery.SharedImage
import java.io.File
import java.io.FileOutputStream

private const val X_ANDROID_PACKAGE = "com.twitter.android"

actual class XShareLauncher(
    private val context: Context
) {
    actual fun shareToX(text: String, image: SharedImage?) {
        val baseIntent = Intent(Intent.ACTION_SEND).apply {
            if (image != null) {
                // 画像がある場合は画像とテキストの両方を設定
                type = "image/*"
                putExtra(Intent.EXTRA_TEXT, text)

                val imageBytes = image.toByteArray()
                if (imageBytes != null) {
                    // 一時ファイルを作成してUriを取得
                    val tempFile =
                        File(
                            context.cacheDir,
                            "share_image_${System.currentTimeMillis()}.jpg"
                        )
                    FileOutputStream(tempFile).use { it.write(imageBytes) }

                    // FileProviderを使用してUriを取得（Android 7.0以降対応）
                    val imageUri =
                        try {
                            FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.fileprovider",
                                tempFile
                            )
                        } catch (e: Exception) {
                            // FileProviderが設定されていない場合は通常のUriを使用
                            Uri.fromFile(tempFile)
                        }

                    putExtra(Intent.EXTRA_STREAM, imageUri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
            } else {
                // 画像がない場合はテキストのみ
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
            }
        }

        try {
            // Xアプリがインストールされている場合は直接開く
            Intent(baseIntent).apply {
                // 旧Twitter公式アプリのパッケージ名
                `package` = X_ANDROID_PACKAGE
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }.let {
                context.startActivity(it)
            }
        } catch (e: ActivityNotFoundException) {
            // Xアプリがインストールされていない場合は通常の共有シートを表示
            Intent.createChooser(baseIntent, "共有先を選択").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }.let {
                context.startActivity(it)
            }
        }
    }
}

@Composable
actual fun createRememberedXShareLauncher(): XShareLauncher {
    val context = LocalContext.current
    return remember { XShareLauncher(context) }
}

