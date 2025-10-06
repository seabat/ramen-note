package dev.seabat.ramennote.domain.di

import dev.seabat.ramennote.domain.usecase.AddShopUseCase
import dev.seabat.ramennote.domain.usecase.AddShopUseCaseContract
import dev.seabat.ramennote.domain.usecase.DeleteAreaUseCase
import dev.seabat.ramennote.domain.usecase.DeleteAreaUseCaseContract
import dev.seabat.ramennote.domain.usecase.DeleteShopAndImageUseCase
import dev.seabat.ramennote.domain.usecase.DeleteShopAndImageUseCaseContract
import dev.seabat.ramennote.domain.usecase.FetchPlaceHolderImageUseCase
import dev.seabat.ramennote.domain.usecase.FetchPlaceHolderImageUseCaseContract
import dev.seabat.ramennote.domain.usecase.FetchUnsplashImageUseCase
import dev.seabat.ramennote.domain.usecase.FetchUnsplashImageUseCaseContract
import dev.seabat.ramennote.domain.usecase.LoadImageUseCase
import dev.seabat.ramennote.domain.usecase.LoadImageUseCaseContract
import dev.seabat.ramennote.domain.usecase.LoadShopUseCase
import dev.seabat.ramennote.domain.usecase.LoadShopUseCaseContract
import dev.seabat.ramennote.domain.usecase.SaveShopMenuImageUseCase
import dev.seabat.ramennote.domain.usecase.SaveShopMenuImageUseCaseContract
import dev.seabat.ramennote.domain.usecase.UpdateAreaUseCase
import dev.seabat.ramennote.domain.usecase.UpdateAreaUseCaseContract
import dev.seabat.ramennote.domain.usecase.UpdateShopUseCase
import dev.seabat.ramennote.domain.usecase.UpdateShopUseCaseContract
import dev.seabat.ramennote.domain.usecase.CreateNoImageUseCase
import dev.seabat.ramennote.domain.usecase.CreateNoImageUseCaseContract
import org.koin.dsl.module

val useCaseModule = module {
    single<AddShopUseCaseContract> { AddShopUseCase(get()) }
    single<CreateNoImageUseCaseContract> { CreateNoImageUseCase(get()) }
    single<DeleteAreaUseCaseContract> { DeleteAreaUseCase(get(), get(), get()) }
    single<DeleteShopAndImageUseCaseContract> { DeleteShopAndImageUseCase(get(), get()) }
    single<FetchPlaceHolderImageUseCaseContract> { FetchPlaceHolderImageUseCase(get(), get()) }
    single<FetchUnsplashImageUseCaseContract> { FetchUnsplashImageUseCase(get(), get()) }
    single<LoadImageUseCaseContract> { LoadImageUseCase(get()) }
    single<LoadImageUseCaseContract> { LoadImageUseCase(get()) }
    single<LoadShopUseCaseContract> { LoadShopUseCase(get()) }
    single<SaveShopMenuImageUseCaseContract> { SaveShopMenuImageUseCase(get()) }
    single<UpdateAreaUseCaseContract> { UpdateAreaUseCase(get(), get(), get()) }
    single<UpdateShopUseCaseContract> { UpdateShopUseCase(get()) }
}