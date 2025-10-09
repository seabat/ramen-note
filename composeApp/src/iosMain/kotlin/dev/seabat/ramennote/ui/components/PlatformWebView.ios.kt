package dev.seabat.ramennote.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.WebKit.WKWebViewConfiguration
import platform.Foundation.NSURL
import platform.Foundation.NSURLRequest
import platform.WebKit.WKWebView

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun PlatformWebView(
    url: String,
    modifier: Modifier
) {
    UIKitView(
        factory = {
            val configuration = WKWebViewConfiguration().apply {
                allowsInlineMediaPlayback = true
                mediaTypesRequiringUserActionForPlayback = platform.WebKit.WKAudiovisualMediaTypeNone
            }
            
            val webView: WKWebView = WKWebView(
                frame = platform.CoreGraphics.CGRectMake(0.0, 0.0, 0.0, 0.0),
                configuration = configuration
            )
            
            // デバッグ用のログ
            println("Loading URL: $url")
            
            val nsUrl: NSURL? = NSURL.URLWithString(url) ?: NSURL.URLWithString("about:blank")
            nsUrl?.let { url: NSURL -> 
                println("NSURL created: $url")
                webView.loadRequest(NSURLRequest.requestWithURL(url))
            }
            webView
        },
        modifier = modifier
    )
}
