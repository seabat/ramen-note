package dev.seabat.ramennote.ui.screens.history

import dev.seabat.ramennote.domain.model.Shop
import dev.seabat.ramennote.ui.gallery.SharedImage
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.LocalDate

interface ReportViewModelContract {
    val image: StateFlow<SharedImage?>

    fun report(menuName: String, reportedDate: LocalDate, impression: String, shop: Shop, image: SharedImage?)
}