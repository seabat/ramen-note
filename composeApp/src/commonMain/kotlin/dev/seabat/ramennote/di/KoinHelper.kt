package dev.seabat.ramennote.di

import dev.seabat.ramennote.data.di.dataSourceModule
import dev.seabat.ramennote.data.di.databaseModule
import dev.seabat.ramennote.data.di.factoryModule
import dev.seabat.ramennote.data.di.repositoryModule
import dev.seabat.ramennote.domain.di.useCaseModule
import dev.seabat.ramennote.ui.di.uiModule
import dev.seabat.ramennote.ui.di.viewModelModule
import org.koin.core.context.startKoin
import org.koin.core.module.Module

fun initKoin(onKoinStart: () -> Module) {
    startKoin {
        modules(
            uiModule,
            viewModelModule,
            repositoryModule,
            databaseModule,
            factoryModule,
            useCaseModule,
            dataSourceModule,
            onKoinStart()
        )
    }
}