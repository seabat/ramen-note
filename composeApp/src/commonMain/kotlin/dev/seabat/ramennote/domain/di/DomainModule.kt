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
import dev.seabat.ramennote.domain.usecase.UpdateAreaImageUseCase
import dev.seabat.ramennote.domain.usecase.UpdateAreaImageUseCaseContract
import dev.seabat.ramennote.domain.usecase.UpdateShopCountInAreaUseCase
import dev.seabat.ramennote.domain.usecase.UpdateShopCountInAreaUseCaseContract
import org.koin.dsl.module
import dev.seabat.ramennote.domain.usecase.AddScheduleUseCase
import dev.seabat.ramennote.domain.usecase.AddScheduleUseCaseContract
import dev.seabat.ramennote.domain.usecase.LoadRecentScheduleUseCase
import dev.seabat.ramennote.domain.usecase.LoadRecentScheduleUseCaseContract
import dev.seabat.ramennote.domain.usecase.SwitchFavoriteUseCase
import dev.seabat.ramennote.domain.usecase.SwitchFavoriteUseCaseContract
import dev.seabat.ramennote.domain.usecase.LoadFavoriteShopsUseCase
import dev.seabat.ramennote.domain.usecase.LoadFavoriteShopsUseCaseContract
import dev.seabat.ramennote.domain.usecase.UpdateStarUseCase
import dev.seabat.ramennote.domain.usecase.UpdateStarUseCaseContract
import dev.seabat.ramennote.domain.usecase.LoadScheduledShopsUseCase
import dev.seabat.ramennote.domain.usecase.LoadScheduledShopsUseCaseContract

val useCaseModule = module {
    single<AddScheduleUseCaseContract> { AddScheduleUseCase(get()) }
    single<AddShopUseCaseContract> { AddShopUseCase(get()) }
    single<CreateNoImageUseCaseContract> { CreateNoImageUseCase(get()) }
    single<DeleteAreaUseCaseContract> { DeleteAreaUseCase(get(), get(), get()) }
    single<DeleteShopAndImageUseCaseContract> { DeleteShopAndImageUseCase(get(), get()) }
    single<FetchPlaceHolderImageUseCaseContract> { FetchPlaceHolderImageUseCase(get(), get()) }
    single<FetchUnsplashImageUseCaseContract> { FetchUnsplashImageUseCase(get(), get()) }
    single<LoadImageUseCaseContract> { LoadImageUseCase(get()) }
    single<LoadImageUseCaseContract> { LoadImageUseCase(get()) }
    single<LoadShopUseCaseContract> { LoadShopUseCase(get()) }
    single<LoadRecentScheduleUseCaseContract> { LoadRecentScheduleUseCase(get()) }
    single<LoadScheduledShopsUseCaseContract> { LoadScheduledShopsUseCase(get()) }
    single<LoadFavoriteShopsUseCaseContract> { LoadFavoriteShopsUseCase(get()) }
    single<SaveShopMenuImageUseCaseContract> { SaveShopMenuImageUseCase(get()) }
    single<SwitchFavoriteUseCaseContract> { SwitchFavoriteUseCase(get()) }
    single<UpdateAreaImageUseCaseContract> { UpdateAreaImageUseCase(get(), get(), get()) }
    single<UpdateAreaUseCaseContract> { UpdateAreaUseCase(get(), get(), get()) }
    single<UpdateShopCountInAreaUseCaseContract> { UpdateShopCountInAreaUseCase(get(), get()) }
    single<UpdateShopUseCaseContract> { UpdateShopUseCase(get()) }
    single<UpdateStarUseCaseContract> { UpdateStarUseCase(get()) }
}