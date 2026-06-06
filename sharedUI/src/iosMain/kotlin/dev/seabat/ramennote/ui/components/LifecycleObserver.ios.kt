package dev.seabat.ramennote.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSNotificationCenter
import platform.UIKit.UIApplicationDidBecomeActiveNotification
import platform.UIKit.UIApplicationWillResignActiveNotification

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun rememberLifecycleState(): State<LifecycleState> {
    val lifecycleState = remember { mutableStateOf(LifecycleState(isResumed = true)) } // デフォルトはtrue（アプリ起動時はフォアグラウンド）

    DisposableEffect(Unit) {
        val notificationCenter = NSNotificationCenter.defaultCenter

        val becomeActiveObserver =
            notificationCenter.addObserverForName(
                name = UIApplicationDidBecomeActiveNotification,
                `object` = null,
                queue = null
            ) { _ ->
                lifecycleState.value = lifecycleState.value.copy(isResumed = true)
            }

        val resignActiveObserver =
            notificationCenter.addObserverForName(
                name = UIApplicationWillResignActiveNotification,
                `object` = null,
                queue = null
            ) { _ ->
                lifecycleState.value = lifecycleState.value.copy(isResumed = false)
            }

        onDispose {
            notificationCenter.removeObserver(becomeActiveObserver)
            notificationCenter.removeObserver(resignActiveObserver)
        }
    }

    return lifecycleState
}
