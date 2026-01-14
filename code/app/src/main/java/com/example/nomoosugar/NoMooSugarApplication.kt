package com.example.nomoosugar

import android.app.Application
import androidx.room.Room
import com.example.nomoosugar.db.NoMooSugarDatabase
import com.example.nomoosugar.repository.SugarRepository

class NoMooSugarApplication : Application() {
    val database: NoMooSugarDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            NoMooSugarDatabase::class.java,
            "nomoo_sugar_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    val sugarRepository: SugarRepository by lazy {
        SugarRepository(database.sugarEntryDao())
    }
}
