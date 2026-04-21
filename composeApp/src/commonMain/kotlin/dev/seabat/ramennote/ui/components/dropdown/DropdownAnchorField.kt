package dev.seabat.ramennote.ui.components.dropdown

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp

/**
 * ドロップダウンのアンカーフィールド。
 *
 * ## OutlinedTextField を使用しない理由
 * `OutlinedTextField` は内部 padding が大きく、隣接する `SearchInputField` と高さが合わなくなる。
 * HistoryScreen の `YearDropdownField` と `SearchInputField` は同一行に並んでおり、
 * 高さの一致が設計上の要件となっている。
 * また、表示内容はヒントテキストで示しているため、`OutlinedTextField` のフローティングラベルは不要。
 *
 * 見た目を変更する場合は `BasicTextField` + 手動ボーダーの構造を維持したまま、
 * ボーダー色・テキスト色を手動で制御すること。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownAnchorField(
    text: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier =
            modifier
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(4.dp)
                ).clickable { onToggle() }
    ) {
        // テキスト入力領域（読み取り専用として扱う）
        BasicTextField(
            value = text,
            onValueChange = { /* readOnly */ },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 28.dp, top = 8.dp, bottom = 8.dp) // 右側にアイコン分の余白
                    .focusable(false),
            readOnly = true,
            singleLine = true,
            textStyle =
                MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
        )

        // 末尾のドロップダウンアイコン
        Box(
            modifier =
                Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 4.dp)
        ) {
            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
        }
    }
}
