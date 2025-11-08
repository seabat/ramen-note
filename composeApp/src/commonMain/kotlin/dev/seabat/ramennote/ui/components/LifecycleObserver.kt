package dev.seabat.ramennote.ui.components

import androidx.compose.runtime.Composable

@Composable
expect fun rememberLifecycleState(): LifecycleState

data class LifecycleState(
    val isResumed: Boolean
)
