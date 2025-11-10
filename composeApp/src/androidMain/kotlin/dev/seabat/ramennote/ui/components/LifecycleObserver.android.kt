package dev.seabat.ramennote.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import dev.seabat.ramennote.domain.util.logd

@Composable
actual fun rememberLifecycleState(): State<LifecycleState> {
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState =
        remember {
            mutableStateOf(
                LifecycleState(
                    isResumed =
                        lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
                )
            )
        }

    androidx.compose.runtime.DisposableEffect(lifecycleOwner) {
        val observer =
            LifecycleEventObserver { _, event ->
                logd(message = "event: $event")
                lifecycleState.value = lifecycleState.value.copy(isResumed = event == Lifecycle.Event.ON_RESUME)
            }
        logd(message = "start observe lifecycle")
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            logd(message = "stop observe lifecycle")
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    return lifecycleState
}
