package com.example.nomoosugar.repository

import com.example.nomoosugar.db.SugarEntryDao
import com.example.nomoosugar.db.SugarEntryEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class SugarRepository(private val dao: SugarEntryDao) {

    suspend fun insert(entry: SugarEntryEntity) {
        dao.insert(entry)
    }

    suspend fun update(entry: SugarEntryEntity) {
        dao.update(entry)
    }

    suspend fun delete(entry: SugarEntryEntity) {
        dao.delete(entry)
    }

    fun getById(id: Long): Flow<SugarEntryEntity?> {
        return dao.getById(id)
    }

    fun getTodayEntries(): Flow<List<SugarEntryEntity>> {
        return dao.getEntriesForDate(LocalDate.now().toEpochDay())
    }

    fun getTodayTotalSugar(): Flow<Double?> {
        return dao.getTotalSugarForDay(LocalDate.now().toEpochDay())
    }
}