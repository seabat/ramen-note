package dev.seabat.ramennote.ui.screens.note.shop

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
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
fun ShopMultilineInputField(
    label: String,
    value: String,
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
            Column(horizontalAlignment = Alignment.End) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier =
                        Modifier
                            .fillMaxWidth()
//                            .heightIn(min = 48.dp)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                    singleLine = false,
                    textStyle =
                        MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default),
                    keyboardActions =
                        KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        )
                )

                // Columnの右下にdeleteアイコンとキーボードを閉じるアイコンを表示
                Row(
                    modifier = Modifier.padding(bottom = 8.dp, end = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (value.isNotEmpty()) {
                        // キーボードを閉じるアイコン
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = "キーボードを閉じる",
                            modifier =
                                Modifier
                                    .size(24.dp)
                                    .padding(end = 8.dp) // Icon の内側に padding を入れてアイコン画像を少し小さくする
                                    .clickable { focusManager.clearFocus() },
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // deleteアイコン
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
}

@Preview
@Composable
fun ShopMultilineInputFieldPreview() {
    RamenNoteTheme {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            ShopMultilineInputField(
                label = "ノート",
                value =
                    "【機能】\n" +
                        "・訪店予定および訪店後のラーメン店をエリアごとにグループ化して管理\n" +
                        "・訪店予定および訪店後のラーメン店の Web サイト URL、Google マップ URL、評価、最寄り駅等の情報を登録\n" +
                        "・お気に入りラーメン店の登録\n" +
                        "・ラーメン店への訪店予定を登録\n" +
                        "・訪店後のラーメンの食レポを登録\n" +
                        "\n" +
                        "【使い方】\n" +
                        "[気になるラーメン店を登録] → [訪店予定を登録] → [訪店当日にマップ情報等を確認] → [訪店後に食レポを登録]",
                onValueChange = {}
            )
        }
    }
}

@Preview
@Composable
fun ShopMultilineInputFieldEmptyPreview() {
    RamenNoteTheme {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            ShopMultilineInputField(
                label = "ノート",
                value = "",
                onValueChange = {}
            )
        }
    }
}
