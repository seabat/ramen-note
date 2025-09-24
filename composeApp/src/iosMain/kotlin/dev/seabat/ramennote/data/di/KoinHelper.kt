package dev.seabat.ramennote.data.di

import dev.seabat.ramennote.ui.di.viewModelModule
import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(
            viewModelModule,
            repositoryModule,
            databaseModule,
            factoryModule,
        )
    }
}