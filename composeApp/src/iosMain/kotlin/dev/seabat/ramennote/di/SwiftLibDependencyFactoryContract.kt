package dev.seabat.ramennote.di

import dev.seabat.ramennote.data.datasource.LocalStorageDataSourceContract
import dev.seabat.ramennote.data.datasource.NoImageDataSourceContract

interface SwiftLibDependencyFactoryContract {
    fun provideLocalStorageDataSourceContract(): LocalStorageDataSourceContract
    fun provideNoImageDataSourceContract(): NoImageDataSourceContract
}