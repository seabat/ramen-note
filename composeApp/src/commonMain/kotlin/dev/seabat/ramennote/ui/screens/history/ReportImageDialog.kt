package dev.seabat.ramennote.ui.screens.history

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.seabat.ramennote.ui.components.dialog.WideDialog

@Composable
fun ReportImageDialog(
    imageBytes: ByteArray?,
    onDismiss: () -> Unit
) {
    WideDialog(onDismiss = onDismiss) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = imageBytes,
                contentDescription = null,
                modifier =
                    Modifier
                        .wrapContentWidth()
                        .wrapContentHeight(),
                contentScale = ContentScale.Fit
            )
        }
    }
}
