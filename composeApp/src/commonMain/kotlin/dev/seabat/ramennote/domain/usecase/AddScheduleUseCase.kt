package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.ShopsRepositoryContract
import kotlinx.datetime.LocalDate

class AddScheduleUseCase(
    private val shopsRepository: ShopsRepositoryContract
) : AddScheduleUseCaseContract {
    override suspend operator fun invoke(shopId: Int, date: LocalDate) {
        val shop = shopsRepository.getShopById(shopId) ?: return
        val updated = shop.copy(scheduledDate = date)
        shopsRepository.updateShop(updated)
    }
}


