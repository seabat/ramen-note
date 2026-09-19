package dev.seabat.ramennote.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.seabat.ramennote.ui.theme.RamenNoteTheme
import org.jetbrains.compose.resources.vectorResource
import ramennote.sharedui.generated.resources.Res
import ramennote.sharedui.generated.resources.kid_star_24px_empty
import ramennote.sharedui.generated.resources.kid_star_24px_fill

@Composable
fun ReportStarIcon(
    onOff: Boolean,
    onClick: (() -> Unit)? = null
) {
    StarIcon(
        modifier = Modifier.size(14.dp),
        onOff = onOff,
        onClick = onClick
    )
}

@Composable
fun ShopStarIcon(
    onOff: Boolean,
    onClick: (() -> Unit)? = null
) {
    StarIcon(
        modifier = Modifier.size(24.dp),
        onOff = onOff,
        onClick = onClick
    )
}

@Composable
private fun StarIcon(
    modifier: Modifier = Modifier,
    onOff: Boolean,
    onClick: (() -> Unit)? = null
) {
    Icon(
        modifier =
            modifier
                .then(
                    if (onClick != null) {
                        Modifier.padding(end = 0.dp).clickable { onClick.invoke() }
                    } else {
                        Modifier
                    }
                ),
        imageVector =
            if (onOff) {
                vectorResource(Res.drawable.kid_star_24px_fill)
            } else {
                vectorResource(Res.drawable.kid_star_24px_empty)
            },
        contentDescription = "星",
        tint = if (onOff) Color(0xFFFFEA00) else Color.Gray.copy(alpha = 0.3f)
    )
}

@Preview
@Composable
fun StarIconOn() {
    RamenNoteTheme {
        StarIcon(onOff = true)
    }
}

@Preview
@Composable
fun StarIconOff() {
    RamenNoteTheme {
        StarIcon(onOff = false, onClick = {})
    }
}
