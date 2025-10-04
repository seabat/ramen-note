package dev.seabat.ramennote.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.seabat.ramennote.ui.theme.RamenNoteTheme
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import ramennote.composeapp.generated.resources.Res
import ramennote.composeapp.generated.resources.kid_star_24px_empty
import ramennote.composeapp.generated.resources.kid_star_24px_fill

@Composable
fun StarIcon(
    onOff: Boolean,
    onClick: (() -> Unit)? = null
) {
    Icon(
        modifier = Modifier
            .then(
                if (onClick != null) {
                    Modifier.padding(end = 4.dp).clickable { onClick.invoke() }
                } else {
                    Modifier
                }
            ),
        imageVector = if (onOff) {
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


