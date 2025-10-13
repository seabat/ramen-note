package dev.seabat.ramennote.ui.screens.note.shop

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.seabat.ramennote.ui.components.StarIcon
import org.jetbrains.compose.resources.stringResource
import ramennote.composeapp.generated.resources.Res
import ramennote.composeapp.generated.resources.add_evaluation_label


@Composable
fun StarRating(
    star: Int,
    onValueChange: (Int) -> Unit
) {
    Column {
        Text(
            text = stringResource(Res.string.add_evaluation_label),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row {
            repeat(3) { index ->
                StarIcon(
                    onOff = index < star,
                    onClick = { onValueChange(index + 1) }
                )
            }
        }
    }
}

