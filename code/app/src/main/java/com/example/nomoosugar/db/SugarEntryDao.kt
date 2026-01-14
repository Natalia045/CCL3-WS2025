package com.example.nomoosugar.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SugarEntryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: SugarEntryEntity)

    @Update
    suspend fun update(entry: SugarEntryEntity)

    @Delete
    suspend fun delete(entry: SugarEntryEntity)

    @Query("SELECT * FROM sugar_entries WHERE id = :id LIMIT 1")
    fun observeById(id: Long): Flow<SugarEntryEntity?>

    @Query("SELECT * FROM sugar_entries ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<SugarEntryEntity>>

    @Query(
        "SELECT * FROM sugar_entries " +
            "WHERE timestamp BETWEEN :startTimestamp AND :endTimestamp " +
            "ORDER BY timestamp ASC",
    )
    fun observeBetween(
        startTimestamp: Long,
        endTimestamp: Long,
    ): Flow<List<SugarEntryEntity>>
}