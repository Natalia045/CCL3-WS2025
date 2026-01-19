package com.example.nomoosugar

import android.app.Application
import androidx.room.Room
import com.example.nomoosugar.db.ChallengeEntity // Import ChallengeEntity
import com.example.nomoosugar.db.FoodEntity
import com.example.nomoosugar.db.NoMooSugarDatabase
import com.example.nomoosugar.db.UserProfileEntity
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
            .also { db ->
                CoroutineScope(Dispatchers.IO).launch {
                    // Load initial foods
                    if (db.foodDao().getCount() == 0) {
                        val jsonString = applicationContext.assets.open("foods.json").bufferedReader().use { it.readText() }
                        val jsonArray = JSONArray(jsonString)
                        val foods = mutableListOf<FoodEntity>()

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)
                            foods.add(
                                FoodEntity(
                                    name = jsonObject.getString("name"),
                                    sugarAmount = jsonObject.getDouble("sugarAmount")
                                )
                            )
                        }
                        db.foodDao().insertAll(foods)
                    }

                    // Load initial challenges (NEW BLOCK)
                    // You'll need to add a getCount() method to ChallengeDao similar to FoodDao.
                    // Or, use db.challengeDao().getAllOnce().isEmpty() for initial check.
                    if (db.challengeDao().getCount() == 0) { // Assuming ChallengeDao has getCount()
                        val jsonString = applicationContext.assets.open("challenges.json").bufferedReader().use { it.readText() }
                        val jsonArray = JSONArray(jsonString)
                        val challenges = mutableListOf<ChallengeEntity>()

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)
                            challenges.add(
                                ChallengeEntity(
                                    challengeType = jsonObject.getInt("challengeType"),
                                    title = jsonObject.getString("title"),
                                    description = jsonObject.getString("description"),
                                    targetCount = jsonObject.getInt("targetCount")
                                    // currentCount, isActive, isCompleted, lastUpdated will use their default values
                                )
                            )
                        }
                        db.challengeDao().insertChallenges(challenges)
                    }

                    // Removed the default user creation block (as per your earlier request)
                    // val userProfileRepository = UserProfileRepository(db.userProfileDao())
                    // if (!userProfileRepository.hasUser().first()) { ... }
                }
            }
    }

    val sugarRepository: SugarRepository by lazy {
        SugarRepository(database.sugarEntryDao())
    }

    val foodRepository: FoodRepository by lazy {
        FoodRepository(database.foodDao())
    }

    val challengeRepository: ChallengeRepository by lazy {
        ChallengeRepository(database.challengeDao())
    }

    val userProfileRepository: UserProfileRepository by lazy {
        UserProfileRepository(database.userProfileDao())
    }
}