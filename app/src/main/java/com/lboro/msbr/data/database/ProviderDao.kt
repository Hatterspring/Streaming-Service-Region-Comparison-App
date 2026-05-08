package com.lboro.msbr.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ProviderDao {
    @Query("SELECT * FROM providers ORDER BY provider_id")
    suspend fun getAll(): List<ProviderEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(provider: ProviderEntry)

    @Delete
    suspend fun delete(provider: ProviderEntry)
}