package dev.seabat.ramennote.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

class AndroidDatabaseFactory(
    private val context: Context
) : DatabaseFactoryContract {
    override fun getBuilder(): RoomDatabase.Builder<RamenNoteDatabase> = getDatabaseBuilder(context)
}

private fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<RamenNoteDatabase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath("my_room.db")
    return Room.databaseBuilder<RamenNoteDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}
