package dev.seabat.ramennote.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSNotificationCenter
import platform.UIKit.UIApplicationDidBecomeActiveNotification
import platform.UIKit.UIApplicationWillResignActiveNotification

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun rememberLifecycleState(): LifecycleState {
    var isResumed by remember { mutableStateOf(true) } // デフォルトはtrue（アプリ起動時はフォアグラウンド）

    DisposableEffect(Unit) {
        val notificationCenter = NSNotificationCenter.defaultCenter

        val becomeActiveObserver =
            notificationCenter.addObserverForName(
                name = UIApplicationDidBecomeActiveNotification,
                `object` = null,
                queue = null
            ) { _ ->
                isResumed = true
            }

        val resignActiveObserver =
            notificationCenter.addObserverForName(
                name = UIApplicationWillResignActiveNotification,
                `object` = null,
                queue = null
            ) { _ ->
                isResumed = false
            }

        onDispose {
            notificationCenter.removeObserver(becomeActiveObserver)
            notificationCenter.removeObserver(resignActiveObserver)
        }
    }

    return LifecycleState(isResumed = isResumed)
}
