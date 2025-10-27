package dev.seabat.ramennote.domain.model

import kotlinx.datetime.LocalDate

data class Schedule(
    val shopId: Int = 0,
    val shopName: String = "",
    val shopUrl: String = "",
    val mapUrl: String = "",
    val star: Int = 0,
    val category: String = "",
    val menuName: String = "",
    val photoName: String = "",
    val scheduledDate: LocalDate? = null,
    val isReported: Boolean = false
) {
    companion object {
        fun fromShop(shop: Shop): Schedule =
            Schedule(
                shopId = shop.id,
                shopName = shop.name,
                shopUrl = shop.shopUrl,
                mapUrl = shop.mapUrl,
                star = shop.star,
                category = shop.category,
                menuName = shop.menuName1,
                photoName = shop.photoName1,
                scheduledDate = shop.scheduledDate,
                isReported = false
            )
    }
}
