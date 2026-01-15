package com.example.nomoosugar.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        SugarEntryEntity::class,
        FoodEntity::class,
    ],
    version = 3,
    exportSchema = false,
)
abstract class NoMooSugarDatabase : RoomDatabase() {
    abstract fun sugarEntryDao(): SugarEntryDao
    abstract fun foodDao(): FoodDao
}