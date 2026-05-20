package com.lboro.msbr.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [MovieDetailsEntry::class], version = 5)
abstract class CompDatabase : RoomDatabase() {
    companion object {
        const val DB_NAME = "db"
        @Volatile
        private var INSTANCE: CompDatabase? = null
        fun getDatabase(context: Context): CompDatabase
        {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CompDatabase::class.java,
                    "Movies"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
    abstract fun movieDetailsDao(): MovieDetailsDao
}