package dev.seabat.ramennote.ui.di

import dev.seabat.ramennote.ui.screens.history.HistoryViewModel
import dev.seabat.ramennote.ui.screens.history.editreport.EditReportViewModel
import dev.seabat.ramennote.ui.screens.history.report.ReportViewModel
import dev.seabat.ramennote.ui.screens.home.HomeViewModel
import dev.seabat.ramennote.ui.screens.note.NoteViewModel
import dev.seabat.ramennote.ui.screens.note.addarea.AddAreaViewModel
import dev.seabat.ramennote.ui.screens.note.addshop.AddShopViewModel
import dev.seabat.ramennote.ui.screens.note.editarea.EditAreaViewModel
import dev.seabat.ramennote.ui.screens.note.editshop.EditShopViewModel
import dev.seabat.ramennote.ui.screens.note.shop.ShopViewModel
import dev.seabat.ramennote.ui.screens.note.shoplist.AreaShopListViewModel
import dev.seabat.ramennote.ui.screens.schedule.ScheduleViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule =
    module {
        viewModel { AddAreaViewModel(get(), get()) }
        viewModel { AddShopViewModel(get(), get(), get(), get()) }
        viewModel { AreaShopListViewModel(get()) }
        viewModel { EditAreaViewModel(get(), get(), get(), get()) }
        viewModel { EditReportViewModel(get(), get(), get(), get()) }
        viewModel { EditShopViewModel(get(), get(), get(), get(), get(), get()) }
        viewModel { HistoryViewModel(get()) }
        viewModel { HomeViewModel(get(), get(), get(), get()) }
        viewModel { NoteViewModel(get(), get()) }
        viewModel { ReportViewModel(get(), get()) }
        viewModel { ShopViewModel(get(), get(), get(), get(), get()) }
        viewModel { ScheduleViewModel(get(), get(), get()) }
    }
