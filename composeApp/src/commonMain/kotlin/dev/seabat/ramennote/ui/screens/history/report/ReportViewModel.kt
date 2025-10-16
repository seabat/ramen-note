package dev.seabat.ramennote.ui.screens.history.report

import androidx.lifecycle.ViewModel
import dev.seabat.ramennote.domain.model.Shop
import dev.seabat.ramennote.ui.gallery.SharedImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.LocalDate

class ReportViewModel : ViewModel(), ReportViewModelContract {
    private val _image = MutableStateFlow<SharedImage?>(null)
    override val image: StateFlow<SharedImage?> = _image.asStateFlow()

    override fun report(
        menuName: String,
        reportedDate: LocalDate,
        impression: String,
        shop: Shop,
        image: SharedImage?
    ) {
        TODO("Not yet implemented")
    }
}