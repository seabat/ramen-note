package dev.seabat.ramennote.ui.screens.note.shopslocation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.github.skydoves.navgraph.annotations.NavDestination
import com.github.skydoves.navgraph.annotations.NavEdge
import com.github.skydoves.navgraph.annotations.NavPreview
import dev.seabat.ramennote.domain.model.Shop
import dev.seabat.ramennote.ui.components.AppBar
import dev.seabat.ramennote.ui.components.AppProgressBar
import dev.seabat.ramennote.ui.components.ShopsMap
import dev.seabat.ramennote.ui.navigation.Screen
import dev.seabat.ramennote.ui.theme.RamenNoteTheme
import org.koin.compose.viewmodel.koinViewModel

@NavDestination(route = Screen.ShopsLocation::class)
@NavEdge(to = Screen.Shop::class, label = "店舗詳細へ")
@Composable
fun ShopsLocationScreen(
    areaId: Int,
    areaName: String,
    onBackClick: () -> Unit,
    onShopClick: (Shop) -> Unit,
    viewModel: ShopsLocationViewModelContract = koinViewModel<ShopsLocationViewModel>()
) {
    val locations by viewModel.locations.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(areaId) {
        viewModel.loadShopsAndResolveLocations(areaId)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        AppBar(
            title = areaName,
            onBackClick = onBackClick
        )

        Box(modifier = Modifier.fillMaxSize()) {
            ShopsMap(
                locations = locations,
                onPinClick = onShopClick,
                modifier = Modifier.fillMaxSize()
            )

            if (isLoading) {
                AppProgressBar()
            }
        }
    }
}

@NavPreview(route = Screen.ShopsLocation::class, primary = true)
@Preview
@Composable
private fun ShopsLocationScreenPreview() {
    RamenNoteTheme {
        ShopsLocationScreen(
            areaId = 1,
            areaName = "東京",
            onBackClick = {},
            onShopClick = {},
            viewModel = MockShopsLocationViewModel()
        )
    }
}
