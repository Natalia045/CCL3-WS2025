package com.example.nomoosugar.repository

import com.example.nomoosugar.db.FoodDao
import com.example.nomoosugar.db.FoodEntity
import kotlinx.coroutines.flow.Flow

class FoodRepository(
    private val foodDao: FoodDao,
) {
    fun searchFoods(query: String): Flow<List<FoodEntity>> {
        return foodDao.searchFoods(query)
    }
}
