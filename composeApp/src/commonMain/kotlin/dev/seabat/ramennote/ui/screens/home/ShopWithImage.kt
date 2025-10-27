package dev.seabat.ramennote.ui.screens.home

import dev.seabat.ramennote.domain.model.Shop

data class ShopWithImage(
    val shop: Shop,
    val imageBytes: ByteArray? = null
)
