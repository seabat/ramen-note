package dev.seabat.ramennote.ui.screens.note.shop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.seabat.ramennote.ui.components.StarIcon
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
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

@Composable
fun StarRatingRow(
    star: Int,
    onValueChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
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

@Preview
@Composable
fun StarRatingPreview() {
    Row(modifier = Modifier.width(200.dp)) {
        StarRating(star = 3){}
    }
}

@Preview
@Composable
fun StarRatingRowPreview() {
    Row(modifier = Modifier.width(200.dp)) {
        StarRatingRow(star = 3){}
    }
}

