package dev.seabat.ramennote.domain.di

import dev.seabat.ramennote.domain.usecase.AddReportUseCase
import dev.seabat.ramennote.domain.usecase.AddReportUseCaseContract
import dev.seabat.ramennote.domain.usecase.AddShopUseCase
import dev.seabat.ramennote.domain.usecase.AddShopUseCaseContract
import dev.seabat.ramennote.domain.usecase.CreateNoImageByteArrayUseCase
import dev.seabat.ramennote.domain.usecase.CreateNoImageByteArrayUseCaseContract
import dev.seabat.ramennote.domain.usecase.CreateNoImageIfNeededUseCase
import dev.seabat.ramennote.domain.usecase.CreateNoImageIfNeededUseCaseContract
import dev.seabat.ramennote.domain.usecase.CreateReportPhotoNameUseCase
import dev.seabat.ramennote.domain.usecase.CreateReportPhotoNameUseCaseContract
import dev.seabat.ramennote.domain.usecase.DeleteAreaUseCase
import dev.seabat.ramennote.domain.usecase.DeleteAreaUseCaseContract
import dev.seabat.ramennote.domain.usecase.DeleteReportUseCase
import dev.seabat.ramennote.domain.usecase.DeleteReportUseCaseContract
import dev.seabat.ramennote.domain.usecase.DeleteScheduleInShopUseCase
import dev.seabat.ramennote.domain.usecase.DeleteScheduleInShopUseCaseContract
import dev.seabat.ramennote.domain.usecase.DeleteShopAndImageUseCase
import dev.seabat.ramennote.domain.usecase.DeleteShopAndImageUseCaseContract
import dev.seabat.ramennote.domain.usecase.FetchAiShopInfoUseCase
import dev.seabat.ramennote.domain.usecase.FetchAiShopUseCaseContract
import dev.seabat.ramennote.domain.usecase.FetchAndSaveUnsplashImageUseCase
import dev.seabat.ramennote.domain.usecase.FetchAndSaveUnsplashImageUseCaseContract
import dev.seabat.ramennote.domain.usecase.FetchPlaceHolderImageUseCase
import dev.seabat.ramennote.domain.usecase.FetchPlaceHolderImageUseCaseContract
import dev.seabat.ramennote.domain.usecase.InquiryAppVersionUseCase
import dev.seabat.ramennote.domain.usecase.InquiryAppVersionUseCaseContract
import dev.seabat.ramennote.domain.usecase.LoadAreaImageUseCase
import dev.seabat.ramennote.domain.usecase.LoadAreaImageUseCaseContract
import dev.seabat.ramennote.domain.usecase.LoadAreasUseCase
import dev.seabat.ramennote.domain.usecase.LoadAreasUseCaseContract
import dev.seabat.ramennote.domain.usecase.LoadFavoriteShopsUseCase
import dev.seabat.ramennote.domain.usecase.LoadFavoriteShopsUseCaseContract
import dev.seabat.ramennote.domain.usecase.LoadFullReportUseCase
import dev.seabat.ramennote.domain.usecase.LoadFullReportUseCaseContract
import dev.seabat.ramennote.domain.usecase.LoadFullReportsByAreaUseCase
import dev.seabat.ramennote.domain.usecase.LoadFullReportsByAreaUseCaseContract
import dev.seabat.ramennote.domain.usecase.LoadFullReportsByShopUseCase
import dev.seabat.ramennote.domain.usecase.LoadFullReportsByShopUseCaseContract
import dev.seabat.ramennote.domain.usecase.LoadFullReportsUseCase
import dev.seabat.ramennote.domain.usecase.LoadFullReportsUseCaseContract
import dev.seabat.ramennote.domain.usecase.LoadImageUseCase
import dev.seabat.ramennote.domain.usecase.LoadImageUseCaseContract
import dev.seabat.ramennote.domain.usecase.LoadRecentScheduleUseCase
import dev.seabat.ramennote.domain.usecase.LoadRecentScheduleUseCaseContract
import dev.seabat.ramennote.domain.usecase.LoadScheduledShopsUseCase
import dev.seabat.ramennote.domain.usecase.LoadScheduledShopsUseCaseContract
import dev.seabat.ramennote.domain.usecase.LoadShopListByAreaUseCase
import dev.seabat.ramennote.domain.usecase.LoadShopListByAreaUseCaseContract
import dev.seabat.ramennote.domain.usecase.LoadShopUseCase
import dev.seabat.ramennote.domain.usecase.LoadShopUseCaseContract
import dev.seabat.ramennote.domain.usecase.LoadThreeMonthsFullReportsUseCase
import dev.seabat.ramennote.domain.usecase.LoadThreeMonthsFullReportsUseCaseContract
import dev.seabat.ramennote.domain.usecase.LoadYearlyReportStatsUseCase
import dev.seabat.ramennote.domain.usecase.LoadYearlyReportStatsUseCaseContract
import dev.seabat.ramennote.domain.usecase.SearchShopsByNameUseCase
import dev.seabat.ramennote.domain.usecase.SearchShopsByNameUseCaseContract
import dev.seabat.ramennote.domain.usecase.SwitchFavoriteUseCase
import dev.seabat.ramennote.domain.usecase.SwitchFavoriteUseCaseContract
import dev.seabat.ramennote.domain.usecase.UpdateAllAreasUseCase
import dev.seabat.ramennote.domain.usecase.UpdateAllAreasUseCaseContract
import dev.seabat.ramennote.domain.usecase.UpdateAreaImageUseCase
import dev.seabat.ramennote.domain.usecase.UpdateAreaImageUseCaseContract
import dev.seabat.ramennote.domain.usecase.UpdateAreaUseCase
import dev.seabat.ramennote.domain.usecase.UpdateAreaUseCaseContract
import dev.seabat.ramennote.domain.usecase.UpdateReportUseCase
import dev.seabat.ramennote.domain.usecase.UpdateReportUseCaseContract
import dev.seabat.ramennote.domain.usecase.UpdateScheduleInShopUseCase
import dev.seabat.ramennote.domain.usecase.UpdateScheduleInShopUseCaseContract
import dev.seabat.ramennote.domain.usecase.UpdateShopCountInAreaUseCase
import dev.seabat.ramennote.domain.usecase.UpdateShopCountInAreaUseCaseContract
import dev.seabat.ramennote.domain.usecase.UpdateShopUseCase
import dev.seabat.ramennote.domain.usecase.UpdateShopUseCaseContract
import dev.seabat.ramennote.domain.usecase.UpdateStarUseCase
import dev.seabat.ramennote.domain.usecase.UpdateStarUseCaseContract
import org.koin.dsl.module

val useCaseModule =
    module {
        single<UpdateScheduleInShopUseCaseContract> { UpdateScheduleInShopUseCase(get()) }
        single<AddShopUseCaseContract> { AddShopUseCase(get(), get(), get()) }
        single<CreateNoImageByteArrayUseCaseContract> { CreateNoImageByteArrayUseCase(get()) }
        single<CreateNoImageIfNeededUseCaseContract> { CreateNoImageIfNeededUseCase(get(), get()) }
        single<CreateReportPhotoNameUseCaseContract> { CreateReportPhotoNameUseCase() }
        single<DeleteAreaUseCaseContract> { DeleteAreaUseCase(get(), get()) }
        single<DeleteReportUseCaseContract> { DeleteReportUseCase(get()) }
        single<DeleteScheduleInShopUseCaseContract> { DeleteScheduleInShopUseCase(get()) }
        single<DeleteShopAndImageUseCaseContract> { DeleteShopAndImageUseCase(get(), get(), get(), get()) }
        single<FetchAiShopUseCaseContract> { FetchAiShopInfoUseCase(get(), get()) }
        single<FetchPlaceHolderImageUseCaseContract> { FetchPlaceHolderImageUseCase(get(), get()) }
        single<FetchAndSaveUnsplashImageUseCaseContract> { FetchAndSaveUnsplashImageUseCase(get(), get()) }
        single<InquiryAppVersionUseCaseContract> { InquiryAppVersionUseCase(get()) }
        single<LoadAreasUseCaseContract> { LoadAreasUseCase(get()) }
        single<LoadAreaImageUseCaseContract> { LoadAreaImageUseCase(get(), get()) }
        single<LoadImageUseCaseContract> { LoadImageUseCase(get()) }
        single<LoadShopListByAreaUseCaseContract> { LoadShopListByAreaUseCase(get(), get()) }
        single<LoadShopUseCaseContract> { LoadShopUseCase(get()) }
        single<LoadRecentScheduleUseCaseContract> { LoadRecentScheduleUseCase(get(), get()) }
        single<LoadFullReportUseCaseContract> { LoadFullReportUseCase(get(), get(), get()) }
        single<LoadFullReportsUseCaseContract> { LoadFullReportsUseCase(get(), get(), get()) }
        single<LoadFullReportsByShopUseCaseContract> { LoadFullReportsByShopUseCase(get(), get(), get()) }
        single<LoadFullReportsByAreaUseCaseContract> { LoadFullReportsByAreaUseCase(get(), get(), get()) }
        single<LoadThreeMonthsFullReportsUseCaseContract> {
            LoadThreeMonthsFullReportsUseCase(
                get(),
                get(),
                get()
            )
        }
        single<LoadYearlyReportStatsUseCaseContract> { LoadYearlyReportStatsUseCase(get()) }
        single<AddReportUseCaseContract> { AddReportUseCase(get(), get(), get()) }
        single<LoadScheduledShopsUseCaseContract> { LoadScheduledShopsUseCase(get(), get()) }
        single<LoadFavoriteShopsUseCaseContract> { LoadFavoriteShopsUseCase(get()) }
        single<SearchShopsByNameUseCaseContract> { SearchShopsByNameUseCase(get()) }
        single<SwitchFavoriteUseCaseContract> { SwitchFavoriteUseCase(get()) }
        single<UpdateAreaImageUseCaseContract> { UpdateAreaImageUseCase(get(), get(), get()) }
        single<UpdateAreaUseCaseContract> { UpdateAreaUseCase(get(), get()) }
        single<UpdateShopCountInAreaUseCaseContract> { UpdateShopCountInAreaUseCase(get(), get()) }
        single<UpdateShopUseCaseContract> { UpdateShopUseCase(get(), get(), get()) }
        single<UpdateAllAreasUseCaseContract> { UpdateAllAreasUseCase(get()) }
        single<UpdateStarUseCaseContract> { UpdateStarUseCase(get()) }
        single<UpdateReportUseCaseContract> { UpdateReportUseCase(get(), get()) }
    }
