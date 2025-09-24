package dev.seabat.ramennote.data.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import dev.seabat.ramennote.data.database.DatabaseFactoryContract
import dev.seabat.ramennote.data.database.RamenNoteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.dsl.module

val databaseModule = module {
    single<RamenNoteDatabase> { getRamenNoteDatabase(get()) }
}

fun getRamenNoteDatabase(
    factory: DatabaseFactoryContract
): RamenNoteDatabase {
    return factory.getBuilder()
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}