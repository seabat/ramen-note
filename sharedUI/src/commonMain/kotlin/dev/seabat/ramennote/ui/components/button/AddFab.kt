package dev.seabat.ramennote.ui.components.button

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AddFab(
    modifier: Modifier = Modifier,
    contentDescription: String = "追加",
    onAddClick: () -> Unit
) {
    FloatingActionButton(
        onClick = { onAddClick() },
        modifier = modifier.padding(16.dp),
        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = contentDescription
        )
    }
}
