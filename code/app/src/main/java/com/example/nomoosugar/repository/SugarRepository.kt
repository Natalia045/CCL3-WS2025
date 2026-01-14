package com.example.nomoosugar.repository

import com.example.nomoosugar.db.SugarEntryDao
import com.example.nomoosugar.db.SugarEntryEntity
import kotlinx.coroutines.flow.Flow

class SugarRepository(
    private val sugarEntryDao: SugarEntryDao,
) {

    suspend fun addEntry(entry: SugarEntryEntity) {
        sugarEntryDao.insert(entry)
    }

    suspend fun updateEntry(entry: SugarEntryEntity) {
        sugarEntryDao.update(entry)
    }

    suspend fun deleteEntry(entry: SugarEntryEntity) {
        sugarEntryDao.delete(entry)
    }

    fun observeEntry(id: Long): Flow<SugarEntryEntity?> {
        return sugarEntryDao.observeById(id)
    }

    fun observeAllEntries(): Flow<List<SugarEntryEntity>> {
        return sugarEntryDao.observeAll()
    }

    fun observeEntriesBetween(
        startTimestamp: Long,
        endTimestamp: Long,
    ): Flow<List<SugarEntryEntity>> {
        return sugarEntryDao.observeBetween(startTimestamp, endTimestamp)
    }
}