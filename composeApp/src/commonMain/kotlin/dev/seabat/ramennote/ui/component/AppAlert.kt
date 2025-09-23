package dev.seabat.ramennote.ui.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun AppAlert(
    message: String,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        confirmButton = {
            Button(
                onClick = {
                    onConfirm() }
            ) {
                Text(text = "OK")
            }
        },
        title = null,
        text = { Text(text = message) }
    )
}