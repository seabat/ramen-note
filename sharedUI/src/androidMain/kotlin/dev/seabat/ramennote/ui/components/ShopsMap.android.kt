package dev.seabat.ramennote.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import dev.seabat.ramennote.domain.model.Shop
import dev.seabat.ramennote.domain.model.ShopLocation

@Composable
actual fun ShopsMap(
    locations: List<ShopLocation>,
    onPinClick: (Shop) -> Unit,
    modifier: Modifier
) {
    val cameraPositionState = rememberCameraPositionState()

    LaunchedEffect(locations) {
        if (locations.isNotEmpty()) {
            val boundsBuilder = LatLngBounds.builder()
            locations.forEach { boundsBuilder.include(LatLng(it.latitude, it.longitude)) }
            val bounds = boundsBuilder.build()
            cameraPositionState.animate(CameraUpdateFactory.newLatLngBounds(bounds, 120))
        }
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState
    ) {
        locations.forEach { shopLocation ->
            Marker(
                state = MarkerState(LatLng(shopLocation.latitude, shopLocation.longitude)),
                title = shopLocation.shop.name,
                snippet = shopLocation.shop.category.ifEmpty { null },
                onInfoWindowClick = { onPinClick(shopLocation.shop) }
            )
        }
    }
}
