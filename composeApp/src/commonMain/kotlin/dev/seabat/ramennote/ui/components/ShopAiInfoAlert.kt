package dev.seabat.ramennote.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.seabat.ramennote.ui.screens.note.shop.ShopInputField
import org.jetbrains.compose.resources.stringResource
import ramennote.composeapp.generated.resources.Res
import ramennote.composeapp.generated.resources.add_shop_alert_button
import ramennote.composeapp.generated.resources.add_shop_alert_enter_shop_name
import ramennote.composeapp.generated.resources.add_shop_alert_shop_name
import ramennote.composeapp.generated.resources.add_shop_alert_warning

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
                Text(text = stringResource(Res.string.add_shop_alert_button))
            }
        },
        title = null,
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(Res.string.add_shop_alert_enter_shop_name)
                )
                Spacer(modifier = Modifier.height(16.dp))
                ShopInputField(
                    label = stringResource(Res.string.add_shop_alert_shop_name),
                    value = shopName,
                    onValueChange = { shopName = it }
                )
                Text(
                    text = stringResource(Res.string.add_shop_alert_warning),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    )
}
