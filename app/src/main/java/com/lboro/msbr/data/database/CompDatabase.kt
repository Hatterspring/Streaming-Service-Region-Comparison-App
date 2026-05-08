package com.lboro.msbr.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ProviderEntry::class, MovieDetailsEntry::class], version = 1)
abstract class CompDatabase : RoomDatabase() {
    companion object {
        const val DB_NAME = "db"
    }

    abstract fun providerDao(): ProviderDao
    abstract fun movieDetailsDao(): MovieDetailsDao
}