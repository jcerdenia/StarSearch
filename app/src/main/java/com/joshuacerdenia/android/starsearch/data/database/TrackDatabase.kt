package com.joshuacerdenia.android.starsearch.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.joshuacerdenia.android.starsearch.data.model.Track

@Database(
    entities = [ Track::class ],
    version = 1
)
abstract class TrackDatabase : RoomDatabase() {

    abstract fun trackDao(): TrackDao
}