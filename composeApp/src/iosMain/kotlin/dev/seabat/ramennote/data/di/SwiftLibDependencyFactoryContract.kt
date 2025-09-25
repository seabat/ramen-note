package dev.seabat.ramennote.data.di

import dev.seabat.ramennote.data.datasource.LocalStorageDataSourceContract

interface SwiftLibDependencyFactoryContract {
    fun provideLocalStorageDataSourceContract(): LocalStorageDataSourceContract
}