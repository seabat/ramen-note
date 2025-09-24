package dev.seabat.ramennote.data.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import dev.seabat.ramennote.data.database.dao.AreaDao
import dev.seabat.ramennote.data.database.entity.AreaEntity

@Database(
    entities = [AreaEntity::class],
    version = 1,
    exportSchema = false
)
@ConstructedBy(RamenNoteDatabaseConstructor::class)
abstract class RamenNoteDatabase : RoomDatabase() {
    abstract fun areaDao(): AreaDao
}

@Suppress("KotlinNoActualForExpect")
expect object RamenNoteDatabaseConstructor : RoomDatabaseConstructor<RamenNoteDatabase> {
    override fun initialize(): RamenNoteDatabase
}



