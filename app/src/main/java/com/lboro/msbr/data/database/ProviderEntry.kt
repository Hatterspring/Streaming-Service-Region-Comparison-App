package com.lboro.msbr.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "providers")
data class ProviderEntry (
    @PrimaryKey val provider_id: String,
    @ColumnInfo(name="name") val name: String,
    /*@ColumnInfo(name="regions") val regions: String,*/
    @ColumnInfo(name="logo") val logo: String? = null,
    @ColumnInfo(name="uniform_resource_identifier") val uniform_resource_identifier: String? = null
)