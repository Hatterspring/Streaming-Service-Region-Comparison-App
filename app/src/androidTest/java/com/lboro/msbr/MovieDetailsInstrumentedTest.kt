package com.lboro.msbr

import android.content.ContentResolver
import android.content.Context
import androidx.annotation.RequiresApi
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.lboro.msbr.data.provider.MovieInfoContract.Movies.CONTENT_URI
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MovieDetailsInstrumentedTest
{
    private lateinit var context: Context
    private lateinit var resolver: ContentResolver
    @Before
    fun setUp()
    {
        /*
        val resolver = context?.contentResolver
        val mProjection: Array<out String?>?
        val mSelectionClause: String?
        val mSelectionArgs: Array<out String>?
        val mSortOrder: String?
        val myInsertValues
        val myUpdateValues*/
        /*@RequiresApi(Build.VERSION_CODES.O)
        val cursor = resolver?.query(CONTENT_URI,
            mProjection, // The columns to return for each row
            mSelectionClause, // Selection criteria
            mSelectionArgs, // Selection criteria
            mSortOrder) // The sort order for the returned rows
        resolver.insert(CONTENT_URI, myInsertValues)
        resolver.update(CONTENT_URI, myUpdateValues)
        resolver.delete(CONTENT_URI)*/
    }
    @Test
    fun testQueryAllMovies() {// Test query the movie database
    }
    @Test
    fun testInsertMovie() { //test insert a movie
    }
    @Test
    fun testDeleteMovie() { //test delete a movie
    }
    @Test(expected = IllegalArgumentException::class
    )
    fun testQueryInvalidUri() {

    }
    @After
    fun tearDown() {// clear the database if needed after test
    }
}