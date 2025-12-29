package dev.seabat.ramennote.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State

@Composable
expect fun rememberLifecycleState(): State<LifecycleState>

data class LifecycleState(
    val isResumed: Boolean
)
