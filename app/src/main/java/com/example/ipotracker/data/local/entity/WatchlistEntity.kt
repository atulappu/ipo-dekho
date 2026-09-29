package com.example.ipotracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchlist")
data class WatchlistEntity(
    @PrimaryKey
    val ipoId: String,
    val addedTimestamp: Long = System.currentTimeMillis()
)
