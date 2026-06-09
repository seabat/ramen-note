package dev.seabat.ramennote.data.di

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import dev.seabat.ramennote.data.database.DatabaseFactoryContract
import dev.seabat.ramennote.data.database.RamenNoteDatabase
import dev.seabat.ramennote.data.repository.AppVersionRepository
import dev.seabat.ramennote.data.repository.AppVersionRepositoryContract
import dev.seabat.ramennote.data.repository.AreaImageRepository
import dev.seabat.ramennote.data.repository.AreaImageRepositoryContract
import dev.seabat.ramennote.data.repository.AreasRepository
import dev.seabat.ramennote.data.repository.AreasRepositoryContract
import dev.seabat.ramennote.data.repository.ExpandShortUrlRepository
import dev.seabat.ramennote.data.repository.ExpandShortUrlRepositoryContract
import dev.seabat.ramennote.data.repository.GeocodingRepository
import dev.seabat.ramennote.data.repository.GeocodingRepositoryContract
import dev.seabat.ramennote.data.repository.GoogleMapSearchUrlRepository
import dev.seabat.ramennote.data.repository.GoogleMapSearchUrlRepositoryContract
import dev.seabat.ramennote.data.repository.LocalImageRepository
import dev.seabat.ramennote.data.repository.LocalImageRepositoryContract
import dev.seabat.ramennote.data.repository.NoImageRepository
import dev.seabat.ramennote.data.repository.NoImageRepositoryContract
import dev.seabat.ramennote.data.repository.ReportsRepository
import dev.seabat.ramennote.data.repository.ReportsRepositoryContract
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
        single<AreasRepositoryContract> { get<RamenNoteDatabase>().let { db -> AreasRepository(db.areaDao(), db) } }
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
        single<ShopsRepositoryContract> { ShopsRepository(get()) }
        single<ShopAiRepositoryContract> { ShopAiRepository(get()) }
        single<GoogleMapSearchUrlRepositoryContract> { GoogleMapSearchUrlRepository() }
        single<GeocodingRepositoryContract> {
            GeocodingRepository(
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
        single<ExpandShortUrlRepositoryContract> {
            // HttpRedirect プラグイン未使用＝リダイレクト非自動追跡。手動で Location ヘッダーを辿る。
            ExpandShortUrlRepository(HttpClient())
        }
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
 *
 * - areas テーブル: name を PK から外し、areaId（AUTOINCREMENT）を PK に変更
 * - shops テーブル: area（String）を areaId（Int）に変更
 * - reports テーブル: areaId カラムを追加
 */
val MIGRATION_5_6 =
    object : Migration(5, 6) {
        override fun migrate(connection: SQLiteConnection) {
            // areas テーブルを再作成（areaId を PK、name を UNIQUE INDEX）
            connection.execSQL(
                """
                CREATE TABLE areas_new (
                    areaId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    name TEXT NOT NULL,
                    count INTEGER NOT NULL,
                    date TEXT NOT NULL,
                    sort INTEGER NOT NULL DEFAULT 1
                )
                """.trimIndent()
            )
            connection.execSQL(
                """
                INSERT INTO areas_new (name, count, date, sort)
                    SELECT name, count, date, sort FROM areas ORDER BY sort
                """.trimIndent()
            )
            connection.execSQL("DROP TABLE areas")
            connection.execSQL("ALTER TABLE areas_new RENAME TO areas")
            connection.execSQL("CREATE UNIQUE INDEX index_areas_name ON areas (name)")

            // shops テーブルを再作成（area String → areaId Int）
            connection.execSQL(
                """
                CREATE TABLE shops_new (
                    id INTEGER PRIMARY KEY NOT NULL,
                    name TEXT NOT NULL DEFAULT '',
                    areaId INTEGER NOT NULL DEFAULT 0,
                    shopUrl TEXT NOT NULL DEFAULT '',
                    mapUrl TEXT NOT NULL DEFAULT '',
                    star INTEGER NOT NULL DEFAULT 0,
                    stationName TEXT NOT NULL DEFAULT '',
                    category TEXT NOT NULL DEFAULT '',
                    scheduledDate TEXT NOT NULL DEFAULT '',
                    menuName1 TEXT NOT NULL DEFAULT '',
                    menuName2 TEXT NOT NULL DEFAULT '',
                    menuName3 TEXT NOT NULL DEFAULT '',
                    photoName1 TEXT NOT NULL DEFAULT '',
                    photoName2 TEXT NOT NULL DEFAULT '',
                    photoName3 TEXT NOT NULL DEFAULT '',
                    description1 TEXT NOT NULL DEFAULT '',
                    description2 TEXT NOT NULL DEFAULT '',
                    description3 TEXT NOT NULL DEFAULT '',
                    favorite INTEGER NOT NULL DEFAULT 0,
                    note TEXT NOT NULL DEFAULT ''
                )
                """.trimIndent()
            )
            connection.execSQL(
                """
                INSERT INTO shops_new
                    SELECT s.id, s.name, COALESCE(a.areaId, 0), s.shopUrl, s.mapUrl, s.star,
                           s.stationName, s.category, s.scheduledDate,
                           s.menuName1, s.menuName2, s.menuName3,
                           s.photoName1, s.photoName2, s.photoName3,
                           s.description1, s.description2, s.description3,
                           s.favorite, s.note
                    FROM shops s LEFT JOIN areas a ON s.area = a.name
                """.trimIndent()
            )
            connection.execSQL("DROP TABLE shops")
            connection.execSQL("ALTER TABLE shops_new RENAME TO shops")

            // reports テーブルに areaId カラムを追加し、shops から補完
            connection.execSQL(
                "ALTER TABLE reports ADD COLUMN areaId INTEGER NOT NULL DEFAULT 0"
            )
            connection.execSQL(
                """
                UPDATE reports SET areaId = (
                    SELECT COALESCE(areaId, 0) FROM shops WHERE shops.id = reports.shopId
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
