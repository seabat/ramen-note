package dev.seabat.ramennote.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIWebView

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun PlatformWebView(
    url: String,
    modifier: Modifier
) {
    UIKitView(
        factory = {
            UIWebView().apply {
                loadRequest(platform.Foundation.NSURLRequest.requestWithURL(platform.Foundation.NSURL.URLWithString(url) ?: platform.Foundation.NSURL.URLWithString("about:blank")!!))
            }
        },
        modifier = modifier
    )
}
