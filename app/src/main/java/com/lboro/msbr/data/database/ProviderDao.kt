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

    @Query("UPDATE providers SET uniform_resource_identifier = :uniform_resource_identifier WHERE name = :name")
    suspend fun updateLinks(name: String, uniform_resource_identifier: String)

    @Query("SELECT uniform_resource_identifier FROM providers WHERE name LIKE :name")
    suspend fun getLink(name: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(provider: ProviderEntry)

    @Delete
    suspend fun delete(provider: ProviderEntry)
}