package dev.seabat.ramennote.ui.screens.componens

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
import dev.seabat.ramennote.ui.components.ReportStarIcon
import dev.seabat.ramennote.ui.components.ShopStarIcon
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import ramennote.sharedui.generated.resources.Res
import ramennote.sharedui.generated.resources.add_evaluation_label

private enum class StarType {
    SHOP,
    REPORT
}

@Composable
fun ShopStarRating(
    star: Int,
    onValueChange: (Int) -> Unit
) {
    StarRating(
        starType = StarType.SHOP,
        repeatTimes = 3,
        star = star,
        onValueChange = onValueChange
    )
}

@Composable
fun ReportStarRating(
    star: Int,
    onValueChange: (Int) -> Unit
) {
    StarRating(
        starType = StarType.REPORT,
        repeatTimes = 5,
        star = star,
        onValueChange = onValueChange
    )
}

@Composable
private fun StarRating(
    starType: StarType,
    repeatTimes: Int,
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
            repeat(repeatTimes) { index ->
                when (starType) {
                    StarType.SHOP ->
                        ShopStarIcon(
                            onOff = index < star,
                            onClick = { onValueChange(index + 1) }
                        )
                    StarType.REPORT ->
                        ReportStarIcon(
                            onOff = index < star,
                            onClick = { onValueChange(index + 1) }
                        )
                }
            }
        }
    }
}

@Composable
fun ShopStarRatingRow(
    star: Int,
    onValueChange: (Int) -> Unit
) {
    StarRatingRow(
        starType = StarType.SHOP,
        repeatTimes = 3,
        star = star,
        onValueChange = onValueChange
    )
}

@Composable
fun ReportStarRatingRow(
    star: Int,
    onValueChange: (Int) -> Unit
) {
    StarRatingRow(
        starType = StarType.REPORT,
        repeatTimes = 5,
        star = star,
        onValueChange = onValueChange
    )
}

@Composable
fun ReportStarEditableRatingRow(
    star: Int,
    onValueChange: (Int) -> Unit
) {
    StarRatingRow(
        starType = StarType.SHOP, // アイコンサイズを SHOP と同じにする
        repeatTimes = 5,
        star = star,
        onValueChange = onValueChange
    )
}

@Composable
private fun StarRatingRow(
    starType: StarType,
    repeatTimes: Int,
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
            repeat(repeatTimes) { index ->
                when (starType) {
                    StarType.SHOP ->
                        ShopStarIcon(
                            onOff = index < star,
                            onClick = { onValueChange(index + 1) }
                        )
                    StarType.REPORT ->
                        ReportStarIcon(
                            onOff = index < star,
                            onClick = { onValueChange(index + 1) }
                        )
                }
            }
        }
    }
}

@Preview
@Composable
fun ShopStarRatingPreview() {
    Row(modifier = Modifier.width(200.dp)) {
        ShopStarRating(star = 3) {}
    }
}

@Preview
@Composable
fun ShopStarRatingRowPreview() {
    Row(modifier = Modifier.width(200.dp)) {
        ShopStarRatingRow(star = 3) {}
    }
}

@Preview
@Composable
fun ReportStarRatingPreview() {
    Row(modifier = Modifier.width(200.dp)) {
        ReportStarRating(star = 0) {}
    }
}

@Preview
@Composable
fun ReportStarRatingRowPreview() {
    Row(modifier = Modifier.width(200.dp)) {
        ReportStarRatingRow(star = 0) {}
    }
}

@Preview
@Composable
fun ReportStarEditableRatingPreview() {
    Row(modifier = Modifier.width(200.dp)) {
        ReportStarEditableRatingRow(star = 0) {}
    }
}
