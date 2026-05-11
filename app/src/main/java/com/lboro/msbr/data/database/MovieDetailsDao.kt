package com.lboro.msbr.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MovieDetailsDao {
    @Query("SELECT * FROM movie_details ORDER BY movie_id")
    suspend fun getAll(): List<MovieDetailsEntry>

    @Query("SELECT * FROM movie_details WHERE name = :movie")
    suspend fun getMovie(movie: String): Array<MovieDetailsEntry>?

    @Query("SELECT name FROM movie_details")
    suspend fun getMovieNames(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movie: MovieDetailsEntry)

    @Query("DELETE FROM movie_details")
    suspend fun clearCache()

    @Delete
    suspend fun delete(movie: MovieDetailsEntry)
}