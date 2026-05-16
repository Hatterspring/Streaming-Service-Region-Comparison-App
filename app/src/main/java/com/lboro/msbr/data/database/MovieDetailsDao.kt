package com.lboro.msbr.data.database

import android.database.Cursor
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
    suspend fun getMovie(movie: String): List<MovieDetailsEntry>

    @Query("SELECT name FROM movie_details")
    suspend fun getMovieNames(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movie: MovieDetailsEntry)

    @Query("DELETE FROM movie_details")
    suspend fun clearCache()

    @Query("DELETE FROM movie_details WHERE name = :movie")
    suspend fun delete(movie: String)

    //for the use of ContentProvider
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMovieDetails(movieDetails: MovieDetailsEntry):Long

    @Delete
    fun deleteMovieDetails(movieDetails: MovieDetailsEntry): Int

    @Query("SELECT * FROM movie_details")
    fun getAllMovieDetailsCursor(): Cursor

    @Query("SELECT * from movie_details WHERE movie_id = :id")
    fun getMovieDetailsItemCursor(id: String): Cursor

    @Query("DELETE FROM movie_details")
    fun clearData()
}