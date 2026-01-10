package dev.seabat.ramennote.ui.screens.history

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.seabat.ramennote.domain.model.FullReport
import dev.seabat.ramennote.ui.components.ReportStarIcon
import dev.seabat.ramennote.ui.share.createPostText
import dev.seabat.ramennote.ui.theme.RamenNoteTheme
import dev.seabat.ramennote.ui.util.dayOfWeekJp
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import ramennote.composeapp.generated.resources.Res
import ramennote.composeapp.generated.resources.ios_share_24px

@Composable
fun ReportCard(
    report: FullReport,
    onTap: () -> Unit = {},
    onLongPress: () -> Unit = {},
    onImageTap: () -> Unit = {},
    onShareTap: (postText: String, imageBytes: ByteArray?) -> Unit = { _, _ -> }
) {
    val date: LocalDate = report.date
    val dayText =
        if (date != null) {
            "${date.dayOfMonth.toString().padStart(2, '0')}日(${dayOfWeekJp(date)})"
        } else {
            ""
        }

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(106.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = { onLongPress() },
                        onTap = { onTap() }
                    )
                },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp).fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = report.imageBytes,
                contentDescription = null,
                modifier =
                    Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onImageTap() },
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier =
                    Modifier
                        .fillMaxHeight()
                        .weight(1.0f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.weight(1.0f),
                        text = report.shopName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Icon(
                        imageVector = vectorResource(Res.drawable.ios_share_24px),
                        contentDescription = null,
                        modifier =
                            Modifier
                                .size(20.dp)
                                .clickable {
                                    onShareTap(
                                        createPostText(
                                            report.shopName,
                                            report.menuName,
                                            report.impression
                                        ),
                                        report.imageBytes
                                    )
                                },
                        tint = LocalContentColor.current
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 日付
                    Text(
                        modifier = Modifier.weight(0.25f),
                        text = dayText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Start
                    )
                    // 評価
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(5) { index ->
                            ReportStarIcon(
                                onOff = index < report.star
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.weight(0.75f),
                        text = report.menuName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (report.impression.isNotBlank()) {
                    Text(
                        text = report.impression,
                        style = MaterialTheme.typography.bodySmall,
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
                report =
                    FullReport(
                        id = 1,
                        shopId = 1,
                        shopName = "一風堂 博多本店",
                        menuName = "白丸元味",
                        photoName = "hakata_ramen_1.jpg",
                        imageBytes = null,
                        impression = "とんこつスープが濃厚で美味しかった。麺も硬めで好みの硬さだった。",
                        date = LocalDate.parse("2024-12-15"),
                        star = 0
                    )
            )

            ReportCard(
                report =
                    FullReport(
                        id = 2,
                        shopId = 2,
                        shopName = "一風堂 山口店",
                        menuName = "赤丸新味 大盛り ライス のり増し",
                        photoName = "hakata_ramen_2.jpg",
                        imageBytes = null,
                        impression = "赤丸新味の辛さがちょうど良く、スープとのバランスが絶妙だった。麺は極細で腰がある。",
                        date = LocalDate.parse("2024-12-10"),
                        star = 2
                    )
            )
        }
    }
}
