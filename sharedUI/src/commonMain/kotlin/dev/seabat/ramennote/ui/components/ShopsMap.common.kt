package dev.seabat.ramennote.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.seabat.ramennote.domain.model.Shop
import dev.seabat.ramennote.domain.model.ShopLocation

@Composable
expect fun ShopsMap(
    locations: List<ShopLocation>,
    onPinClick: (Shop) -> Unit,
    modifier: Modifier = Modifier
)
