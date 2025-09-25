package dev.seabat.ramennote.ui.di

import dev.seabat.ramennote.data.di.databaseModule
import dev.seabat.ramennote.data.di.factoryModule
import dev.seabat.ramennote.data.di.repositoryModule
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