package com.example.msbr.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import com.example.msbr.ui.comparison.CompViewModel.ServiceByRegion

@Entity(tableName = "movie_details")
data class MovieDetailsEntry (
    @PrimaryKey val movie_id: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "image") val image: String,
    @ColumnInfo(name = "release_date") val release_date: String,
    @ColumnInfo(name = "rating") val rating: String,
    @ColumnInfo(name = "available") val available: Boolean,
    @ColumnInfo(name = "service_info") val service_info: String,
)