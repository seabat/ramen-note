package dev.seabat.ramennote.ui.screens.history

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.seabat.ramennote.ui.components.dialog.WideDialog

/**
 * 食レポの画像をフルサイズで表示するダイアログ。
 *
 * [WideDialog] 上に [AsyncImage] を配置し、画像を画面幅いっぱいに引き伸ばさず
 * アスペクト比を維持したまま表示する。
 *
 * @param model Coil に渡す画像モデル。`file://` 形式の URI 文字列または [ByteArray] を受け付ける。
 *              `null` の場合は何も表示されない。
 * @param onDismiss ダイアログを閉じる際に呼び出されるコールバック。
 */
@Composable
fun ReportImageDialog(
    model: Any?,
    onDismiss: () -> Unit
) {
    WideDialog(onDismiss = onDismiss) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = model,
                contentDescription = null,
                modifier =
                    Modifier
                        .wrapContentWidth()
                        .wrapContentHeight(),
                contentScale = ContentScale.Fit
            )
        }
    }
}
