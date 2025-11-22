package dev.seabat.ramennote.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.seabat.ramennote.ui.screens.note.shop.ShopInputField

@Composable
fun ShopAiInfoAlert(
    onConfirm: (String) -> Unit
) {
    var shopName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = {},
        confirmButton = {
            Button(
                onClick = { onConfirm(shopName) }
            ) {
                Text(text = "はい")
            }
        },
        title = null,
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "店名を入力してください。Web サイト等の情報を店名をヒントにしてを自動入力します。"
                )
                Spacer(modifier = Modifier.height(16.dp))
                ShopInputField(
                    label = "店名",
                    value = shopName,
                    onValueChange = { shopName = it }
                )
            }
        }
    )
}

