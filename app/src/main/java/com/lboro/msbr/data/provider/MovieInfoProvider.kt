package com.lboro.msbr.data.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.lboro.msbr.data.database.CompDatabase
import com.lboro.msbr.data.database.MovieDetailsDao
import com.lboro.msbr.data.provider.MovieInfoProvider.MovieInfoContract.MovieDetails.COLUMN_ID
import com.lboro.msbr.data.provider.MovieInfoProvider.MovieInfoContract.MovieDetails.COLUMN_NAME
import com.lboro.msbr.data.provider.MovieInfoProvider.MovieInfoContract.MovieDetails.CONTENT_URI

class MovieInfoProvider: ContentProvider() {
    private lateinit var movieDetailsDao: MovieDetailsDao

    override fun onCreate(): Boolean {
        movieDetailsDao = CompDatabase.getDatabase(context!!).movieDetailsDao()
        return true
    }

    companion object MovieInfoContract {
        const val AUTHORITY = "com.lboro.msbr.provider"
        val BASE_CONTENT_URI = Uri.parse("content://$AUTHORITY")

        private const val MOVIE_DETAILS = 100
        private const val MOVIE_ID = 101

        object MovieDetails {
            const val PATH_MOVIE_DETAILS = "MovieDetails"
            val CONTENT_URI: Uri = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_MOVIE_DETAILS)
            val INVALID_URI: Uri = Uri.withAppendedPath(BASE_CONTENT_URI, "Memes")

            const val CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.$AUTHORITY.$PATH_MOVIE_DETAILS"
            const val CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.$AUTHORITY.$PATH_MOVIE_DETAILS"
            const val COLUMN_ID = "movie_id"
            const val COLUMN_NAME = "name"
            const val COLUMN_DESCRIPTION = "description"
            const val COLUMN_IMAGE = "image"
            const val COLUMN_RELEASE_DATE = "release_date"
            const val COLUMN_RATING = "rating"
            const val COLUMN_SERVICE_INFO = "service_info"
        }
    }

    private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
        addURI(MovieInfoContract.AUTHORITY, MovieInfoContract.MovieDetails.PATH_MOVIE_DETAILS, MOVIE_DETAILS)
        addURI(MovieInfoContract.AUTHORITY,
            "${MovieInfoContract.MovieDetails.PATH_MOVIE_DETAILS}/#", MOVIE_ID)
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder:
    String?): Cursor? {
        val match = uriMatcher.match(uri)
        return when (match) {
            MOVIE_DETAILS -> movieDetailsDao.getAllMovieDetailsCursor()
            MOVIE_ID -> {
                val id = ContentUris.parseId(uri).toString()
                movieDetailsDao.getMovieDetailsItemCursor(id)
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        } as Cursor?
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        throw UnsupportedOperationException("Update operation is not supported")
    }

    override fun delete(
        uri: Uri,
        selection: String?,
        selectionArgs: Array<out String?>?
    ): Int {
        TODO("Not yet implemented")
    }

    override fun getType(uri: Uri): String? {
        return null /*when (uriMatcher.match(uri)) {
            MOVIES -> MovieInfoContract.Movies.CONTENT_TYPE
            MOVIE_ID -> MovieInfoContract.Movies.CONTENT_ITEM_TYPE
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }*/
    }

    override fun insert(
        uri: Uri,
        values: ContentValues?
    ): Uri? {
        TODO("Not yet implemented")
    }

    /*override fun query(
        uri: Uri,
        projection: Array<out String?>?,
        selection: String?,
        selectionArgs: Array<out String?>?,
        sortOrder: String?
    ): Cursor? {
        TODO("Not yet implemented")
    }*/

    /*
    val resolver = context?.contentResolver
    val mProjection: Array<out String?>?
    val mSelectionClause: String?
    val mSelectionArgs: Array<out String>?
    val mSortOrder: String?


    @RequiresApi(Build.VERSION_CODES.O)
    val cursor = resolver?.query(CONTENT_URI,
        mProjection, // The columns to return for each row
        mSelectionClause, // Selection criteria
        mSelectionArgs, // Selection criteria
        mSortOrder) // The sort order for the returned rows
    resolver.insert(CONTENT_URI, myInsertValues)
    resolver.update(CONTENT_URI, myUpdateValues)
    resolver.delete(CONTENT_URI)*/

    val resolver = context?.contentResolver
    val mProjection: Array<String>? = arrayOf(COLUMN_ID, COLUMN_NAME)
    val mSelectionClause: String? = "*"
    val mSelectionArgs: Array<String>? = null
    val mSortOrder: String? = null
    /*val myInsertValues
    val myUpdateValues*/
    val cursor = resolver?.query(CONTENT_URI,
        mProjection, // The columns to return for each row
        mSelectionClause, // Selection criteria
        mSelectionArgs, // Selection criteria
        mSortOrder) // The sort order for the returned rows
    /*resolver.insert(CONTENT_URI, myInsertValues)
    resolver.update(CONTENT_URI, myUpdateValues)
    resolver.delete(CONTENT_URI)*/

}