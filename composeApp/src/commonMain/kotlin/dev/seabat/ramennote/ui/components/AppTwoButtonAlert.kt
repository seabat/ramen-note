package dev.seabat.ramennote.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun AppTwoButtonAlert(
    confirmButtonText: String,
    nagativeButtonText: String,
    message: String,
    onConfirm: () -> Unit,
    onNegative: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        confirmButton = {
            Button(
                onClick = { onConfirm() }
            ) {
                Text(text = confirmButtonText)
            }
        },
        dismissButton = {
            Button(
                onClick = { onNegative() }
            ) {
                Text(text = nagativeButtonText)
            }
        },
        title = null,
        text = { Text(text = message) }
    )
}