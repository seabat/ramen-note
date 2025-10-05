package dev.seabat.ramennote.ui.di

import dev.seabat.ramennote.ui.screens.note.addarea.AddAreaViewModel
import dev.seabat.ramennote.ui.screens.note.editarea.EditAreaViewModel
import dev.seabat.ramennote.ui.screens.note.NoteViewModel
import dev.seabat.ramennote.ui.screens.note.addshop.AddShopViewModel
import dev.seabat.ramennote.ui.screens.note.shop.ShopViewModel
import dev.seabat.ramennote.ui.screens.note.editshop.EditShopViewModel
import dev.seabat.ramennote.ui.screens.note.shoplist.AreaShopListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { EditAreaViewModel(get(), get(), get(), get()) }
    viewModel { AddAreaViewModel(get(), get()) }
    viewModel { NoteViewModel(get(), get()) }
    viewModel { AddShopViewModel(get(), get(), get()) }
    viewModel { EditShopViewModel(get(), get(), get(), get()) }
    viewModel { AreaShopListViewModel(get()) }
    viewModel { ShopViewModel(get(), get()) }
}
