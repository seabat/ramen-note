package dev.seabat.ramennote.di

import dev.seabat.ramennote.data.datasource.LocalStorageDataSourceContract
import dev.seabat.ramennote.data.datasource.NoImageDataSourceContract
import dev.seabat.ramennote.data.datasource.ShopAiDataSourceContract
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

fun createSwiftLibDependencyModule(factory: SwiftLibDependencyFactoryContract): Module =
    module {
        single { factory.provideLocalStorageDataSourceContract() } bind LocalStorageDataSourceContract::class
        single { factory.provideNoImageDataSourceContract() } bind NoImageDataSourceContract::class
        single { factory.provideShopAiDataSourceContract() } bind ShopAiDataSourceContract::class
    }
