package com.joshuacerdenia.android.starsearch.data

import android.content.Context
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
import java.util.concurrent.Executors

private const val TAG = "TrackRepository"

class TrackRepository private constructor(
    context: Context,
    private val iTunesApi: ITunesApi,
    private val trackDao: TrackDao
) {

    companion object {
        // Ensure that there is only one instance of the repository
        private var INSTANCE: TrackRepository? = null

        fun initialize(
            context: Context,
            iTunesApi: ITunesApi,
            trackDao: TrackDao
        ) {
            if (INSTANCE == null) {
                INSTANCE = TrackRepository(context, iTunesApi, trackDao)
            }
        }

        fun getInstance(): TrackRepository {
            return INSTANCE ?: throw IllegalStateException("TrackRepository must be initialized")
        }
    }

    private val connectionChecker = ConnectionChecker(context)
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

    private fun fetchTracksRemotely(
        request: Call<ITunesResponse>
    ): MutableLiveData<List<TrackMinimal>> {
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
                    resultsLiveData.value = lightenTracks(tracks) // Get minimal data for UI
                }
            }
        })

        return resultsLiveData
    }

    private fun lightenTracks(tracks: List<Track>): List<TrackMinimal> {
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

    private fun updateCache(tracks: List<Track>) {
        executor.execute {
            trackDao.replaceTracks(tracks)
        }
    }
}