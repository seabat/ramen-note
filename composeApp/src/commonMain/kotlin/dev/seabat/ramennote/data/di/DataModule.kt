package dev.seabat.ramennote.data.di

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import dev.seabat.ramennote.data.database.DatabaseFactoryContract
import dev.seabat.ramennote.data.database.RamenNoteDatabase
import dev.seabat.ramennote.data.repository.AreaImageRepository
import dev.seabat.ramennote.data.repository.AreaImageRepositoryContract
import dev.seabat.ramennote.data.repository.AppVersionRepository
import dev.seabat.ramennote.data.repository.AppVersionRepositoryContract
import dev.seabat.ramennote.data.repository.AreasRepository
import dev.seabat.ramennote.data.repository.AreasRepositoryContract
import dev.seabat.ramennote.data.repository.GoogleMapSearchUrlRepository
import dev.seabat.ramennote.data.repository.GoogleMapSearchUrlRepositoryContract
import dev.seabat.ramennote.data.repository.LocalImageRepository
import dev.seabat.ramennote.data.repository.LocalImageRepositoryContract
import dev.seabat.ramennote.data.repository.NoImageRepository
import dev.seabat.ramennote.data.repository.NoImageRepositoryContract
import dev.seabat.ramennote.data.repository.ReportsRepository
import dev.seabat.ramennote.data.repository.ReportsRepositoryContract
import dev.seabat.ramennote.data.repository.SettingsRepository
import dev.seabat.ramennote.data.repository.SettingsRepositoryContract
import dev.seabat.ramennote.data.repository.ShopAiRepository
import dev.seabat.ramennote.data.repository.ShopAiRepositoryContract
import dev.seabat.ramennote.data.repository.ShopsRepository
import dev.seabat.ramennote.data.repository.ShopsRepositoryContract
import dev.seabat.ramennote.data.repository.UnsplashImageRepository
import dev.seabat.ramennote.data.repository.UnsplashImageRepositoryContract
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.dsl.module

val databaseModule =
    module {
        single<RamenNoteDatabase> { getRamenNoteDatabase(get()) }
    }

expect val dataSourceModule: Module

expect val factoryModule: Module

val repositoryModule =
    module {
        single<AppVersionRepositoryContract> { AppVersionRepository() }
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
        single<LocalImageRepositoryContract> { LocalImageRepository(get()) }
        single<NoImageRepositoryContract> { NoImageRepository(get()) }
        single<ReportsRepositoryContract> { ReportsRepository(get()) }
        single<SettingsRepositoryContract> { SettingsRepository(get()) }
        single<ShopsRepositoryContract> { ShopsRepository(get()) }
        single<ShopAiRepositoryContract> { ShopAiRepository(get()) }
        single<GoogleMapSearchUrlRepositoryContract> { GoogleMapSearchUrlRepository() }
    }

/**
 * データベースバージョンを 3 から 4 にマイグレーションする
 */
val MIGRATION_3_4 =
    object : Migration(3, 4) {
        override fun migrate(connection: SQLiteConnection) {
            connection.execSQL("ALTER TABLE shops ADD COLUMN note TEXT NOT NULL DEFAULT ''")
        }
    }

/**
 * データベースバージョンを 4 から 5 にマイグレーションする
 */
val MIGRATION_4_5 =
    object : Migration(4, 5) {
        override fun migrate(connection: SQLiteConnection) {
            connection.execSQL("ALTER TABLE areas ADD COLUMN sort INTEGER NOT NULL DEFAULT 1")
        }
    }

/**
 * データベースバージョンを 5 から 6 にマイグレーションする
 */
val MIGRATION_5_6 =
    object : Migration(5, 6) {
        override fun migrate(connection: SQLiteConnection) {
            connection.execSQL(
                """
                CREATE TABLE IF NOT EXISTS settings (
                    key TEXT NOT NULL PRIMARY KEY,
                    value TEXT NOT NULL
                )
                """.trimIndent()
            )
        }
    }

fun getRamenNoteDatabase(
    factory: DatabaseFactoryContract
): RamenNoteDatabase =
    factory
        .getBuilder()
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .addMigrations(MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6)
        .build()
