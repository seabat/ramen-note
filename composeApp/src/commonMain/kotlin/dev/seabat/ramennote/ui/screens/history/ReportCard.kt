package dev.seabat.ramennote.ui.screens.history

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.seabat.ramennote.domain.model.FullReport
import dev.seabat.ramennote.ui.util.dayOfWeekJp
import dev.seabat.ramennote.ui.theme.RamenNoteTheme
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ReportCard(
    report: FullReport,
    onLongPress: () -> Unit = {}
) {
    val date: LocalDate? = report.date
    val dayText = if (date != null) {
        "${date.dayOfMonth.toString().padStart(2, '0')} (${dayOfWeekJp(date)})"
    } else {
        ""
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(135.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { onLongPress() }
                )
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = report.imageBytes,
                contentDescription = null,
                modifier = Modifier.size(100.dp), //TODO: adjust size
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1.0f)
            ) {
                Text(
                    text = report.shopName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = report.menuName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = dayText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (report.impression.isNotBlank()) {
                    Text(
                        text = report.impression,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun ReportCardPreview() {
    RamenNoteTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ReportCard(
                report = FullReport(
                    id = 1,
                    shopId = 1,
                    shopName = "一風堂 博多本店",
                    menuName = "白丸元味",
                    photoName = "hakata_ramen_1.jpg",
                    imageBytes = null,
                    impression = "とんこつスープが濃厚で美味しかった。麺も硬めで好みの硬さだった。",
                    date = LocalDate.parse("2024-12-15"),
                    star = 1
                )
            )
            
            ReportCard(
                report = FullReport(
                    id = 2,
                    shopId = 2,
                    shopName = "一風堂 山口店",
                    menuName = "赤丸新味",
                    photoName = "hakata_ramen_2.jpg",
                    imageBytes = null,
                    impression = "赤丸新味の辛さがちょうど良く、スープとのバランスが絶妙だった。",
                    date = LocalDate.parse("2024-12-10"),
                    star = 2
                )
            )
        }
    }
}