package dev.seabat.ramennote.data.database

import androidx.room.RoomDatabase

interface DatabaseFactoryContract {
    fun getBuilder(): RoomDatabase.Builder<RamenNoteDatabase>
}
