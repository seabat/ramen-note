package dev.seabat.ramennote.data.di

import dev.seabat.ramennote.data.database.DatabaseFactoryContract
import dev.seabat.ramennote.data.database.IosDatabaseFactory
import org.koin.dsl.module

actual val factoryModule = module {
    single<DatabaseFactoryContract> { IosDatabaseFactory() }
}
