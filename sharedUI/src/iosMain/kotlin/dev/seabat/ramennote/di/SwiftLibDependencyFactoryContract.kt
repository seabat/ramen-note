package dev.seabat.ramennote.di

import dev.seabat.ramennote.data.datasource.LocalStorageDataSourceContract
import dev.seabat.ramennote.data.datasource.NoImageDataSourceContract
import dev.seabat.ramennote.data.datasource.ShopAiDataSourceContract

interface SwiftLibDependencyFactoryContract {
    fun provideLocalStorageDataSourceContract(): LocalStorageDataSourceContract

    fun provideNoImageDataSourceContract(): NoImageDataSourceContract

    fun provideShopAiDataSourceContract(): ShopAiDataSourceContract
}
