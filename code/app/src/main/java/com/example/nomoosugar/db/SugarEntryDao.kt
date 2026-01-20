package com.example.nomoosugar.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SugarEntryDao {
    @Insert
    suspend fun insert(entry: SugarEntryEntity)

    @Update
    suspend fun update(entry: SugarEntryEntity)

    @Delete
    suspend fun delete(entry: SugarEntryEntity)

    @Query("SELECT * FROM sugar_entries WHERE id = :id")
    fun getById(id: Int): Flow<SugarEntryEntity?>

    @Query("SELECT * FROM sugar_entries WHERE timestamp = :timestamp")
    fun getEntriesForDate(timestamp: Long): Flow<List<SugarEntryEntity>>

    @Query("SELECT SUM(amount) FROM sugar_entries WHERE timestamp = :timestamp")
    fun getTotalSugarForDay(timestamp: Long): Flow<Double?>
}