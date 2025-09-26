package dev.seabat.ramennote.ui.di

import dev.seabat.ramennote.ui.screens.note.addarea.AddAreaViewModel
import dev.seabat.ramennote.ui.screens.note.editarea.EditAreaViewModel
import dev.seabat.ramennote.ui.screens.note.NoteViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { EditAreaViewModel(get(), get(), get()) }
    viewModel { AddAreaViewModel(get(), get()) }
    viewModel { NoteViewModel(get()) }
}
