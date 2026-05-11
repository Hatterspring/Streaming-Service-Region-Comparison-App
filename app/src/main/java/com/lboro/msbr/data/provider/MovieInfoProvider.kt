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
import com.lboro.msbr.data.provider.MovieInfoProvider.MovieInfoContract.Movies.CONTENT_URI

class MovieInfoProvider: ContentProvider() {
    private lateinit var movieDao: MovieDetailsDao

    override fun onCreate(): Boolean {
        movieDao = CompDatabase.getDatabase(context!!).movieDetailsDao()
        return true
    }

    companion object MovieInfoContract {
        const val AUTHORITY = "com.lboro.msbr.provider"
        val BASE_CONTENT_URI = Uri.parse("content://$AUTHORITY")

        private const val MOVIES = 100
        private const val MOVIE_ID = 101

        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(MovieInfoContract.AUTHORITY, MovieInfoContract.Movies.PATH_MOVIES, MOVIES)
            addURI(MovieInfoContract.AUTHORITY,
                "${MovieInfoContract.Movies.PATH_MOVIES}/#", MOVIE_ID)
        }

        object Movies {
            const val PATH_MOVIES = "Movies"
            val CONTENT_URI: Uri = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_MOVIES)

            const val CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.$AUTHORITY.$PATH_MOVIES"
            const val CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.$AUTHORITY.$PATH_MOVIES"
            const val COLUMN_ID = "id"
            const val COLUMN_NAME = "name"
            const val COLUMN_RATING = "rating"

        }
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder:
    String?): Cursor? {
        val match = uriMatcher.match(uri)
        return when (match) {
            //MOVIES -> movieDao.getAll()
            /*MOVIE_ID -> {
                val id = ContentUris.parseId(uri)
                movieDao.getMovieItemCursor(id.toInt())
            }*/

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

}