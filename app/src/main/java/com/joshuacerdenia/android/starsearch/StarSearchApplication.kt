package com.joshuacerdenia.android.starsearch

import android.app.Application
import androidx.room.Room
import com.joshuacerdenia.android.starsearch.data.TrackFetcher
import com.joshuacerdenia.android.starsearch.data.api.ITunesApi
import com.joshuacerdenia.android.starsearch.data.database.TrackDatabase
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class StarSearchApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://itunes.apple.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val iTunesApi = retrofit.create(ITunesApi::class.java)

        val database = Room.databaseBuilder(this,
            TrackDatabase::class.java,
            "track-database")
            .build()
        val dao = database.trackDao()

        TrackFetcher.initialize(this, iTunesApi, dao)
    }
}