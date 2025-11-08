package dev.seabat.ramennote.ui.screens.note.shop

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import dev.seabat.ramennote.ui.theme.RamenNoteTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import ramennote.composeapp.generated.resources.Res
import ramennote.composeapp.generated.resources.delete_24px

@Composable
fun ShopInputField(
    label: String,
    value: String,
    singleLine: Boolean = true,
    onValueChange: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current

    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(4.dp))

        // OutlinedTextField は内部パディングが大きいので BasicTextField で代用
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(4.dp)
                    )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier =
                        Modifier
                            .weight(1f)
//                            .heightIn(min = 48.dp)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                    singleLine = singleLine,
                    textStyle =
                        MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions =
                        KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        )
                )

                // 右側にdeleteアイコンを表示
                if (value.isNotEmpty()) {
                    Icon(
                        painter = painterResource(Res.drawable.delete_24px),
                        contentDescription = "クリア",
                        modifier =
                            Modifier
                                .size(24.dp)
                                .padding(end = 8.dp) // Icon の内側に padding を入れてアイコン画像を少し小さくする
                                .clickable { onValueChange("") },
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun ShopInputField() {
    RamenNoteTheme {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            ShopInputField(
                label = "店舗名",
                value = "ラーメン太郎",
                onValueChange = {}
            )
        }
    }
}
