package dev.seabat.ramennote.data.di

import dev.seabat.ramennote.data.database.AndroidDatabaseFactory
import dev.seabat.ramennote.data.database.DatabaseFactoryContract
import dev.seabat.ramennote.data.datasource.AndroidLocalStorageDataSource
import dev.seabat.ramennote.data.datasource.AndroidNoImageDataSource
import dev.seabat.ramennote.data.datasource.LocalStorageDataSourceContract
import dev.seabat.ramennote.data.datasource.NoImageDataSourceContract
import dev.seabat.ramennote.data.datasource.ShopAiDataSource
import dev.seabat.ramennote.data.datasource.ShopAiDataSourceContract
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val dataSourceModule =
    module {
        single<LocalStorageDataSourceContract> { AndroidLocalStorageDataSource(context = androidContext()) }
        single<NoImageDataSourceContract> { AndroidNoImageDataSource(context = androidContext()) }
        single<ShopAiDataSourceContract> { ShopAiDataSource() }
    }

actual val factoryModule =
    module {
        single<DatabaseFactoryContract> { AndroidDatabaseFactory(context = androidContext()) }
    }
