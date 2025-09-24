package dev.seabat.ramennote.data.di

import dev.seabat.ramennote.data.database.AndroidDatabaseFactory
import dev.seabat.ramennote.data.database.DatabaseFactoryContract
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val factoryModule = module {
    single<DatabaseFactoryContract> { AndroidDatabaseFactory(context = androidContext()) }
}
