package dev.seabat.ramennote.ui.di

import dev.seabat.ramennote.ui.viewmodel.AddAreaViewModel
import dev.seabat.ramennote.ui.viewmodel.EditAreaViewModel
import dev.seabat.ramennote.ui.viewmodel.NoteViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { EditAreaViewModel(get()) }
    viewModel { AddAreaViewModel(get()) }
    viewModel { NoteViewModel(get()) }
}
