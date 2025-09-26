package dev.seabat.ramennote.data.di

import dev.seabat.ramennote.data.datasource.AndroidLocalStorageDataSource
import dev.seabat.ramennote.data.datasource.LocalStorageDataSourceContract
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val dataSourceModule = module {
    single<LocalStorageDataSourceContract> { AndroidLocalStorageDataSource(context = androidContext()) }
}