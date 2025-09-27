package dev.seabat.ramennote.data.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import dev.seabat.ramennote.data.database.DatabaseFactoryContract
import dev.seabat.ramennote.data.database.RamenNoteDatabase
import dev.seabat.ramennote.data.repository.AreaImageRepository
import dev.seabat.ramennote.data.repository.AreaImageRepositoryContract
import dev.seabat.ramennote.data.repository.AreasRepository
import dev.seabat.ramennote.data.repository.AreasRepositoryContract
import dev.seabat.ramennote.data.repository.LocalAreaImageRepository
import dev.seabat.ramennote.data.repository.LocalAreaImageRepositoryContract
import dev.seabat.ramennote.data.repository.UnsplashImageRepository
import dev.seabat.ramennote.data.repository.UnsplashImageRepositoryContract
import dev.seabat.ramennote.domain.usecase.FetchImageUseCase
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.dsl.module

val databaseModule = module {
    single<RamenNoteDatabase> { getRamenNoteDatabase(get()) }
}

expect val dataSourceModule: Module

expect val factoryModule: Module

val repositoryModule = module {
    single<AreasRepositoryContract> { AreasRepository(get()) }
    single<AreaImageRepositoryContract> {
        AreaImageRepository(
            HttpClient {
                install(ContentNegotiation) { json() }
            }
        )
    }
    single<UnsplashImageRepositoryContract> {
        UnsplashImageRepository(
            HttpClient {
                install(ContentNegotiation) {
                    json(
                        Json {
                            ignoreUnknownKeys = true
                            isLenient = true
                        }
                    )
                }
            }
        )
    }
    single<LocalAreaImageRepositoryContract> { LocalAreaImageRepository(get()) }
    single { FetchImageUseCase(get(), get()) }
}

fun getRamenNoteDatabase(
    factory: DatabaseFactoryContract
): RamenNoteDatabase {
    return factory.getBuilder()
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}