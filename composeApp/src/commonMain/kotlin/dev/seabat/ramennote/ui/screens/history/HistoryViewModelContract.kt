package dev.seabat.ramennote.ui.screens.history

import dev.seabat.ramennote.domain.model.FullReport
import dev.seabat.ramennote.ui.gallery.SharedImage
import dev.seabat.ramennote.ui.share.XShareLauncher
import kotlinx.coroutines.flow.StateFlow

interface HistoryViewModelContract {
    val reports: StateFlow<List<FullReport>>

    fun loadReports(shopId: Int? = null)

    fun shareToX(postText: String, image: SharedImage?, xShareLauncher: XShareLauncher)
}
