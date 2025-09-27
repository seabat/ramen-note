package dev.seabat.ramennote.data.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import dev.seabat.ramennote.data.database.dao.AreaDao
import dev.seabat.ramennote.data.database.dao.ShopDao
import dev.seabat.ramennote.data.database.entity.AreaEntity
import dev.seabat.ramennote.data.database.entity.ShopEntity

@Database(
    entities = [AreaEntity::class, ShopEntity::class],
    version = 2,
    exportSchema = false
)
@ConstructedBy(RamenNoteDatabaseConstructor::class)
abstract class RamenNoteDatabase : RoomDatabase() {
    abstract fun areaDao(): AreaDao
    abstract fun shopDao(): ShopDao
}

@Suppress("KotlinNoActualForExpect")
expect object RamenNoteDatabaseConstructor : RoomDatabaseConstructor<RamenNoteDatabase> {
    override fun initialize(): RamenNoteDatabase
}



