package dev.seabat.ramennote.di

import dev.seabat.ramennote.data.datasource.LocalStorageDataSourceContract
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

fun createSwiftLibDependencyModule(factory: SwiftLibDependencyFactoryContract): Module = module {
    single { factory.provideLocalStorageDataSourceContract() } bind LocalStorageDataSourceContract::class
}