package dev.seabat.ramennote.domain.model

import kotlinx.datetime.LocalDate

data class Schedule(
    val shopId: Int = 0,
    val shopName: String = "",
    val shopUrl: String = "",
    val mapUrl: String = "",
    val star: Int = 0,
    val category: String = "",
    val scheduledDate: LocalDate? = null,
    val isReported : Boolean = false
) {
    companion object {
        fun fromShop(shop: Shop): Schedule {
            return Schedule(
                shopId = shop.id,
                shopName = shop.name,
                shopUrl = shop.shopUrl,
                mapUrl = shop.mapUrl,
                star = shop.star,
                category = shop.category,
                scheduledDate = shop.scheduledDate,
                isReported = false
            )
        }
    }
}
