package dev.seabat.ramennote.ui.screens.note.shop

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.seabat.ramennote.ui.util.createFormattedDateString
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource
import ramennote.composeapp.generated.resources.Res
import ramennote.composeapp.generated.resources.report_select_date

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun DateSelectItem(
    date: LocalDate,
    showDatePicker: () -> Unit
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(Res.string.report_select_date),
            style = MaterialTheme.typography.titleMedium,
            color = Color.Blue,
            modifier = Modifier.clickable { showDatePicker() }
        )
        Text(
            text = createFormattedDateString(date),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}
