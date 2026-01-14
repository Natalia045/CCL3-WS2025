package com.example.nomoosugar.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        SugarEntryEntity::class,
    ],
    version = 2,
    exportSchema = false,
)
abstract class NoMooSugarDatabase : RoomDatabase() {
    abstract fun sugarEntryDao(): SugarEntryDao
}