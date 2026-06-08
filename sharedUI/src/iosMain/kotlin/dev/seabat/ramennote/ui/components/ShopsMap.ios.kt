package dev.seabat.ramennote.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import dev.seabat.ramennote.domain.model.Shop
import dev.seabat.ramennote.domain.model.ShopLocation
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.MapKit.MKCoordinateRegionMakeWithDistance
import platform.MapKit.MKMapView
import platform.MapKit.MKPointAnnotation

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun ShopsMap(
    locations: List<ShopLocation>,
    onPinClick: (Shop) -> Unit,
    modifier: Modifier
) {
    val locationsState = rememberUpdatedState(locations)
    // Kotlin/Native の GC による回収を防ぐため Kotlin 側でも強参照を保持する
    val annotationRefs = remember { mutableListOf<MKPointAnnotation>() }

    UIKitView(
        factory = { MKMapView() },
        update = { mapView ->
            val currentLocations = locationsState.value

            mapView.removeAnnotations(mapView.annotations)
            annotationRefs.clear()

            currentLocations.forEach { shopLocation ->
                val annotation =
                    MKPointAnnotation().apply {
                        setCoordinate(
                            CLLocationCoordinate2DMake(shopLocation.latitude, shopLocation.longitude)
                        )
                        setTitle(shopLocation.shop.name)
                        setSubtitle(shopLocation.shop.stationName.ifEmpty { shopLocation.shop.category })
                    }
                annotationRefs.add(annotation)
                mapView.addAnnotation(annotation)
            }

            if (currentLocations.isNotEmpty()) {
                fitMapToLocations(mapView, currentLocations)
            }
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalForeignApi::class)
private fun fitMapToLocations(
    mapView: MKMapView,
    locations: List<ShopLocation>
) {
    if (locations.isEmpty()) return

    var minLat = locations[0].latitude
    var maxLat = locations[0].latitude
    var minLng = locations[0].longitude
    var maxLng = locations[0].longitude

    locations.forEach { loc ->
        if (loc.latitude < minLat) minLat = loc.latitude
        if (loc.latitude > maxLat) maxLat = loc.latitude
        if (loc.longitude < minLng) minLng = loc.longitude
        if (loc.longitude > maxLng) maxLng = loc.longitude
    }

    val centerLat = (minLat + maxLat) / 2.0
    val centerLng = (minLng + maxLng) / 2.0
    val latDelta = (maxLat - minLat) * 1.5 + 0.01
    val lngDelta = (maxLng - minLng) * 1.5 + 0.01
    // MKCoordinateRegionMakeWithDistance の spanMeters が大きすぎると Invalid Region でクラッシュするため上限を設ける
    val spanMeters = minOf(maxOf(latDelta, lngDelta) * 111_000.0, 10_000_000.0)

    val region =
        MKCoordinateRegionMakeWithDistance(
            CLLocationCoordinate2DMake(centerLat, centerLng),
            spanMeters,
            spanMeters
        )
    mapView.setRegion(region, animated = true)
}
