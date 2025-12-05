package dev.seabat.ramennote.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ShopAiInfo(
    val shopName: String = "",
    val shopUrl: String = "",
    val mapUrl: String = "",
    val stationName: String = "",
    val category: String = ""
)
