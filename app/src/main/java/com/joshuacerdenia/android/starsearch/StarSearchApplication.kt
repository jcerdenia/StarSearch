package com.joshuacerdenia.android.starsearch

import android.app.Application
import androidx.room.Room
import com.joshuacerdenia.android.starsearch.data.TrackFetcher
import com.joshuacerdenia.android.starsearch.data.database.TrackDatabase

class StarSearchApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val database = Room.databaseBuilder(
            this,
            TrackDatabase::class.java,
            "track-database")
            .build()
        val trackDao = database.trackDao()

        TrackFetcher.initialize(this, trackDao)
    }
}