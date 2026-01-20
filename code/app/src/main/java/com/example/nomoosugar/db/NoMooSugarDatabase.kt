package com.example.nomoosugar.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        SugarEntryEntity::class,
        FoodEntity::class,
        ChallengeEntity::class,
        UserProfileEntity::class
    ],
    version = 10,
    exportSchema = false,
)
abstract class NoMooSugarDatabase : RoomDatabase() {
    abstract fun sugarEntryDao(): SugarEntryDao
    abstract fun foodDao(): FoodDao
    abstract fun challengeDao(): ChallengeDao
    abstract fun userProfileDao(): UserProfileDao

}