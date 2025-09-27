package dev.seabat.ramennote.di

import dev.seabat.ramennote.data.datasource.LocalStorageDataSourceContract

interface SwiftLibDependencyFactoryContract {
    fun provideLocalStorageDataSourceContract(): LocalStorageDataSourceContract
}