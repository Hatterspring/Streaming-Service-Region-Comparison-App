package com.example.msbr.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MovieDetailsDao {
    @Query("SELECT * FROM movie_details ORDER BY movie_id")
    suspend fun getAll(): List<MovieDetailsEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(provider: MovieDetailsEntry)

    @Delete
    suspend fun delete(provider: MovieDetailsEntry)
}