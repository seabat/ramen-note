package dev.seabat.ramennote.domain.di

import dev.seabat.ramennote.domain.usecase.FetchImageUseCase
import dev.seabat.ramennote.domain.usecase.FetchImageUseCaseContract
import dev.seabat.ramennote.domain.usecase.LoadImageUseCase
import dev.seabat.ramennote.domain.usecase.LoadImageUseCaseContract
import org.koin.dsl.module

val useCaseModule = module {
    single<FetchImageUseCaseContract> { FetchImageUseCase(get(), get()) }
    single<LoadImageUseCaseContract> { LoadImageUseCase(get()) }
}