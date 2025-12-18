package dev.seabat.ramennote.ui.share

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import dev.seabat.ramennote.ui.gallery.SharedImage
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.dataWithBytes
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIImage

actual class XShareLauncher {
    @OptIn(ExperimentalForeignApi::class)
    actual fun shareToX(text: String, image: SharedImage?) {
        val viewController = UIApplication.sharedApplication.keyWindow?.rootViewController
        if (viewController == null) {
            println("[RamenNote]: XShareLauncher: viewController is null")
            return
        }

        val items = mutableListOf<Any>()

        // テキストを追加
        items.add(text)

        // 画像がある場合はUIImageを追加
        if (image != null) {
            val imageBytes = image.toByteArray()
            if (imageBytes != null) {
                // ByteArrayからUIImageを作成
                val nsData =
                    imageBytes.usePinned { pinned ->
                        NSData.dataWithBytes(pinned.addressOf(0), imageBytes.size.toULong())
                    }
                val uiImage = UIImage.imageWithData(nsData)
                if (uiImage != null) {
                    items.add(uiImage)
                }
            }
        }

        // UIActivityViewControllerを作成して表示
        val activityViewController =
            UIActivityViewController(
                activityItems = items.toList(),
                applicationActivities = null
            )
        // TODO: iPad対応（popoverPresentationControllerの設定）

        viewController.presentViewController(
            viewControllerToPresent = activityViewController,
            animated = true,
            completion = null
        )
    }
}

@Composable
actual fun createRememberedXShareLauncher(): XShareLauncher = remember { XShareLauncher() }
