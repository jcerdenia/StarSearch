package com.joshuacerdenia.android.starsearch.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.joshuacerdenia.android.starsearch.data.api.ITunesApi
import com.joshuacerdenia.android.starsearch.data.api.ITunesResponse
import com.joshuacerdenia.android.starsearch.data.database.TrackDao
import com.joshuacerdenia.android.starsearch.data.model.Track
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executors

private const val TAG = "TrackFetcher"

class TrackFetcher private constructor(
    private val context: Context,
    private val iTunesApi: ITunesApi,
    private val dao: TrackDao
) {

    companion object {
        // Limit TrackFetcher to only one instance
        private var INSTANCE: TrackFetcher? = null

        fun initialize(
            context: Context,
            iTunesApi: ITunesApi,
            dao: TrackDao
        ) {
            if (INSTANCE == null) {
                INSTANCE = TrackFetcher(context, iTunesApi, dao)
            }
        }

        fun get(): TrackFetcher {
            return INSTANCE ?: throw IllegalStateException("TrackFetcher must be initialized")
        }
    }

    private val executor = Executors.newSingleThreadExecutor()

    fun getTracks(): LiveData<List<Track>> {
        val request: Call<ITunesResponse> = iTunesApi.fetchTracks()
        return if (isOnline()) {
            fetchTracksRemotely(request)
        } else {
            dao.getTracks()
        }
    }

    fun updateCache(tracks: List<Track>) {
        executor.execute {
            dao.replaceTracks(tracks)
        }
    }

    private fun fetchTracksRemotely(
        request: Call<ITunesResponse>
    ): MutableLiveData<List<Track>> {
        val resultsLiveData = MutableLiveData<List<Track>>()

        request.enqueue(object : Callback<ITunesResponse> {
            override fun onFailure(call: Call<ITunesResponse>, t: Throwable) {
                Log.i(TAG, "Request failed. Returning cached data", t)
                resultsLiveData.value = dao.getTracks().value
            }

            override fun onResponse(
                call: Call<ITunesResponse>,
                response: Response<ITunesResponse>
            ) {
                resultsLiveData.value = response.body()?.tracks
            }
        })

        return resultsLiveData
    }

    private fun isOnline(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            return networkInfo.isConnected
        }
    }
}