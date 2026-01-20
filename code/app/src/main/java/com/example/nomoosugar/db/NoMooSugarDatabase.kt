package com.example.nomoosugar.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        SugarEntryEntity::class,
        FoodEntity::class,
        UserProfileEntity::class,
        ChallengeEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class NoMooSugarDatabase : RoomDatabase() {
    abstract fun sugarEntryDao(): SugarEntryDao
    abstract fun foodDao(): FoodDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun challengeDao(): ChallengeDao
}