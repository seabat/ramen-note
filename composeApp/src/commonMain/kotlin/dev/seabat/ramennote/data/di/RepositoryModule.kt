package dev.seabat.ramennote.data.di

import dev.seabat.ramennote.data.repository.AreasRepository
import dev.seabat.ramennote.data.repository.AreasRepositoryContract
import org.koin.dsl.module

val repositoryModule = module {
    single<AreasRepositoryContract> { AreasRepository(get()) }
}
