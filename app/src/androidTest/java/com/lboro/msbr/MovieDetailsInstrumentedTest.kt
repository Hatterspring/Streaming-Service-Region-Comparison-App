package com.lboro.msbr

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.lboro.msbr.data.provider.MovieInfoProvider.MovieInfoContract.MovieDetails.COLUMN_DESCRIPTION
import com.lboro.msbr.data.provider.MovieInfoProvider.MovieInfoContract.MovieDetails.COLUMN_ID
import com.lboro.msbr.data.provider.MovieInfoProvider.MovieInfoContract.MovieDetails.COLUMN_IMAGE
import com.lboro.msbr.data.provider.MovieInfoProvider.MovieInfoContract.MovieDetails.COLUMN_NAME
import com.lboro.msbr.data.provider.MovieInfoProvider.MovieInfoContract.MovieDetails.COLUMN_RATING
import com.lboro.msbr.data.provider.MovieInfoProvider.MovieInfoContract.MovieDetails.COLUMN_RELEASE_DATE
import com.lboro.msbr.data.provider.MovieInfoProvider.MovieInfoContract.MovieDetails.COLUMN_SERVICE_INFO
import com.lboro.msbr.data.provider.MovieInfoProvider.MovieInfoContract.MovieDetails.CONTENT_URI
import com.lboro.msbr.data.provider.MovieInfoProvider.MovieInfoContract.MovieDetails.INVALID_URI
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
        context = ApplicationProvider.getApplicationContext()
        resolver = context.contentResolver

    }
    @Test
    fun testQueryAllMovies() {// Test query the movie database
        resolver.query(CONTENT_URI,
            arrayOf(COLUMN_ID, COLUMN_NAME, COLUMN_DESCRIPTION, COLUMN_IMAGE, COLUMN_RELEASE_DATE, COLUMN_RATING, COLUMN_SERVICE_INFO), // The columns to return for each row
            "*", // Selection criteria
            null, // Selection criteria
            null)
    }
    @Test(expected = UnsupportedOperationException::class)
    fun testInsertMovie() { //test insert a movie
        resolver.insert(CONTENT_URI,
            ContentValues()
        )
    }
    @Test(expected = UnsupportedOperationException::class)
    fun testUpdateMovie() {
        resolver.update(CONTENT_URI,
            ContentValues(),
            Bundle())
    }
    @Test(expected = UnsupportedOperationException::class)
    fun testDeleteMovie() { //test delete a movie
        resolver.delete(CONTENT_URI,
            Bundle())
    }
    @Test(expected = IllegalArgumentException::class)
    fun testQueryInvalidUri() {
        resolver.query(INVALID_URI,
            arrayOf(COLUMN_ID, COLUMN_NAME, COLUMN_DESCRIPTION, COLUMN_IMAGE, COLUMN_RELEASE_DATE, COLUMN_RATING, COLUMN_SERVICE_INFO), // The columns to return for each row
            "*", // Selection criteria
            null, // Selection criteria
            null)
    }
    @Test
    fun testGetType() {
        resolver.getType(CONTENT_URI)
    }
    @After
    fun tearDown() {
    }
}