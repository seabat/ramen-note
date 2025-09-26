package dev.seabat.ramennote.data.di

import dev.seabat.ramennote.data.repository.AreasRepository
import dev.seabat.ramennote.data.repository.AreasRepositoryContract
import dev.seabat.ramennote.data.repository.AreaImageRepository
import dev.seabat.ramennote.data.repository.AreaImageRepositoryContract
import dev.seabat.ramennote.data.repository.UnsplashImageRepository
import dev.seabat.ramennote.data.repository.LocalAreaImageRepository
import dev.seabat.ramennote.data.repository.LocalAreaImageRepositoryContract
import dev.seabat.ramennote.data.repository.UnsplashImageRepositoryContract
import dev.seabat.ramennote.domain.usecase.FetchImageUseCase
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okio.FileSystem
import okio.SYSTEM
import org.koin.dsl.module

val repositoryModule = module {
    single<AreasRepositoryContract> { AreasRepository(get()) }
    single { HttpClient { install(ContentNegotiation) { json() } } }
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
