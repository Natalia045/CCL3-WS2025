package com.example.nomoosugar

import android.app.Application
import androidx.room.Room
import com.example.nomoosugar.db.ChallengeEntity
import com.example.nomoosugar.db.FoodEntity
import com.example.nomoosugar.db.NoMooSugarDatabase
import com.example.nomoosugar.repository.ChallengeRepository
import com.example.nomoosugar.repository.FoodRepository
import com.example.nomoosugar.repository.SugarRepository
import com.example.nomoosugar.repository.UserProfileRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray

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

    val userProfileRepository: UserProfileRepository by lazy {
        UserProfileRepository(database.userProfileDao())
    }

    val sugarRepository: SugarRepository by lazy {
        SugarRepository(database.sugarEntryDao())
    }

    val foodRepository: FoodRepository by lazy {
        FoodRepository(database.foodDao())
    }

    val challengeRepository: ChallengeRepository by lazy {
        ChallengeRepository(database.challengeDao(), userProfileRepository)
    }

    override fun onCreate() {
        super.onCreate()

        CoroutineScope(Dispatchers.IO).launch {

            // --- Load initial foods ---
            if (database.foodDao().getCount() == 0) {
                val jsonString = assets.open("foods.json").bufferedReader().use { it.readText() }
                val jsonArray = JSONArray(jsonString)
                val foods = mutableListOf<FoodEntity>()
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    foods.add(FoodEntity(name = obj.getString("name"), sugarAmount = obj.getDouble("sugarAmount")))
                }
                database.foodDao().insertAll(foods)
            }

            // --- Load initial challenges ---
            if (database.challengeDao().getCount() == 0) {
                val jsonString = assets.open("challenges.json").bufferedReader().use { it.readText() }
                val jsonArray = JSONArray(jsonString)
                val challenges = mutableListOf<ChallengeEntity>()
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    challenges.add(
                        ChallengeEntity(
                            challengeType = obj.getInt("challengeType"),
                            title = obj.getString("title"),
                            description = obj.getString("description"),
                            targetCount = obj.getInt("targetCount")
                        )
                    )
                }

                // ✅ CALL THE EXISTING FUNCTION
                challengeRepository.insertChallenges(challenges)
            }
        }
    }
}
