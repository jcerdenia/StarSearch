package com.joshuacerdenia.android.starsearch.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.joshuacerdenia.android.starsearch.data.api.ITunesApi
import com.joshuacerdenia.android.starsearch.data.api.ITunesResponse
import com.joshuacerdenia.android.starsearch.data.database.TrackDao
import com.joshuacerdenia.android.starsearch.data.model.Track
import com.joshuacerdenia.android.starsearch.data.model.TrackMinimal
import com.joshuacerdenia.android.starsearch.utils.ConnectionChecker
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executors

class TrackFetcher private constructor(
    private val trackDao: TrackDao,
    private val connectionChecker: ConnectionChecker
) {

// This class acts as a repository and handles retrieving data from the iTunes API.
// Reference to DAO and ability to check internet connection are needed for caching.

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesApi: ITunesApi = retrofit.create(ITunesApi::class.java)
    private val executor = Executors.newSingleThreadExecutor()

    fun getTrackList(): LiveData<List<TrackMinimal>> {
        return if (connectionChecker.isOnline()) {
            val request = iTunesApi.fetchTracks()
            fetchTracksRemotely(request)
        } else {
            trackDao.getTracksMinimal()
        }
    }

    fun getTrackDetailsById(id: String): LiveData<Track?> {
        return trackDao.getTrackById(id)
    }

    private fun updateCache(tracks: List<Track>) {
        executor.execute {
            trackDao.replaceTracks(tracks)
        }
    }

    private fun fetchTracksRemotely(request: Call<ITunesResponse>): MutableLiveData<List<TrackMinimal>> {
        val resultsLiveData = MutableLiveData<List<TrackMinimal>>()

        request.enqueue(object : Callback<ITunesResponse> {
            override fun onFailure(call: Call<ITunesResponse>, t: Throwable) {
                // Return cached data instead
                resultsLiveData.value = trackDao.getTracksMinimal().value
            }

            override fun onResponse(
                call: Call<ITunesResponse>,
                response: Response<ITunesResponse>
            ) {
                response.body()?.tracks?.let { tracks: List<Track> ->
                    updateCache(tracks) // Cache full track data
                    resultsLiveData.value = lightenTracks(tracks)
                }
            }
        })

        return resultsLiveData
    }

    fun lightenTracks(tracks: List<Track>): List<TrackMinimal> {
        // Get only minimum data needed for UI
        return tracks.map { track ->
            TrackMinimal(
                id = track.id,
                name = track.name,
                album = track.album,
                artwork = track.artwork,
                genre = track.genre,
                price = track.price
            )
        }
    }

    companion object {
        // Ensure that there is only ever one active instance of this class
        private var INSTANCE: TrackFetcher? = null

        fun initialize(
            trackDao: TrackDao,
            connectionChecker: ConnectionChecker
        ) {
            if (INSTANCE == null) {
                INSTANCE = TrackFetcher(trackDao, connectionChecker)
            }
        }

        fun getInstance(): TrackFetcher {
            return INSTANCE ?: throw IllegalStateException("TrackFetcher must be initialized")
        }
    }
}