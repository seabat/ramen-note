package dev.seabat.ramennote.ui.screens.note.shop

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import dev.seabat.ramennote.ui.gallery.SharedImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import ramennote.composeapp.generated.resources.Res
import ramennote.composeapp.generated.resources.add_shop_menu_name_label
import ramennote.composeapp.generated.resources.add_shop_no_image
import ramennote.composeapp.generated.resources.add_shop_select_button

@Composable
fun RamenField(
    menuName: String = "",
    sharedImage: SharedImage?,
    enablePermission: () -> Unit,
    onMenuValueChange: (String) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        // メインのBox
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                ShopInputField(
                    label = stringResource(Res.string.add_shop_menu_name_label),
                    value = menuName,
                    onValueChange = onMenuValueChange
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 写真
                PhotoSelectionField(
                    sharedImage = sharedImage,
                    onClick = enablePermission
                )
            }
        }

        // タイトルをborder上に配置
        Text(
            text = "メニュー情報",
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 16.dp, y = (-8).dp) // 位置調整
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 4.dp),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun PhotoSelectionField(
    sharedImage: SharedImage? = null,
    onClick: () -> Unit
) {
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    LaunchedEffect(sharedImage) {
        val image = withContext(Dispatchers.IO) {
            sharedImage?.toImageBitmap()
        }
        imageBitmap = image
    }

    Column {
//        Text(
//            text = stringResource(Res.string.add_shop_photo_label),
//            style = MaterialTheme.typography.bodyLarge,
//            fontWeight = FontWeight.Medium
//        )

        Spacer(modifier = Modifier.height(8.dp))

        // 写真選択
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(Res.string.add_shop_select_button),
                style = MaterialTheme.typography.titleMedium,
                color = Color.Blue,
                modifier = Modifier.clickable { onClick() }
            )
            if (imageBitmap != null) {
                Image(
                    modifier = Modifier
                        .size(120.dp),
                    bitmap = imageBitmap!!,
                    contentDescription = null
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(Res.string.add_shop_no_image),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun RamenPreview() {
   MaterialTheme {
       RamenField(
           menuName = "チャーシューメン",
           sharedImage = null,
           enablePermission = {},
           onMenuValueChange = {}
       )
   }
}