package com.joshuacerdenia.android.starsearch.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.joshuacerdenia.android.starsearch.data.TrackFetcher
import com.joshuacerdenia.android.starsearch.data.model.TrackMinimal
import java.util.*

class TrackListViewModel: ViewModel() {

    private val trackFetcher = TrackFetcher.getInstance()

    var currentOrder = 0
        private set
    var currentQuery = ""
        get() = field.toLowerCase(Locale.ROOT)
        private set

    private val sourceTracksLiveData: LiveData<List<TrackMinimal>> = trackFetcher.getTrackList()
    val tracksLiveData = MediatorLiveData<List<TrackMinimal>>()

    init {
        // MediatorLiveData allows the ViewModel to manipulate data as needed by UI
        tracksLiveData.addSource(sourceTracksLiveData) { source ->
            tracksLiveData.value = if (source != null) {
                sortTracks(source, currentOrder)
            } else {
                emptyList()
            }
        }
    }

    fun submitQuery(query: String) {
        currentQuery = query
        sourceTracksLiveData.value?.let { tracks ->
            if (currentQuery.isNotEmpty()) {
                tracksLiveData.value = filterTracks(
                    sortTracks(tracks, currentOrder),
                    currentQuery
                )
            } else {
                tracksLiveData.value = sortTracks(tracks, currentOrder)
            }
        }
    }

    fun changeOrder(order: Int) {
        currentOrder = order
        sourceTracksLiveData.value?.let { tracks ->
            tracksLiveData.value = sortTracks(
                filterTracks(tracks, currentQuery),
                order
            )
        }
    }

    private fun sortTracks(tracks: List<TrackMinimal>, order: Int): List<TrackMinimal> {
        return when (order) {
            ORDER_GENRE -> tracks.sortedBy { it.genre }
            ORDER_NAME -> tracks.sortedBy { it.name ?: it.album }
            ORDER_PRICE_ASCENDING -> tracks.sortedBy { it.price }
            ORDER_PRICE_DESCENDING -> tracks.sortedByDescending { it.price }
            else -> {
                // Get tracks in default order as retrieved from repository
                sourceTracksLiveData.value?.let {
                    filterTracks(it, currentQuery)
                } ?: tracks
            }
        }
    }

    private fun filterTracks(tracks: List<TrackMinimal>, query: String): List<TrackMinimal> {
        val filteredTracks = mutableListOf<TrackMinimal>()
        for (track in tracks) {
            if (track.genre != null) {
                if (track.genre.toLowerCase(Locale.ROOT).contains(query)) {
                    filteredTracks.add(track)
                } else if (track.name != null) {
                    if (track.name.toLowerCase(Locale.ROOT).contains(query)) {
                        filteredTracks.add(track)
                    }
                } else if (track.album != null) {
                    if (track.album.toLowerCase(Locale.ROOT).contains(query)) {
                        filteredTracks.add(track)
                    }
                }
            }
        }
        return filteredTracks
    }

    companion object {
        private const val ORDER_NAME = 1
        private const val ORDER_GENRE = 2
        private const val ORDER_PRICE_ASCENDING = 3
        private const val ORDER_PRICE_DESCENDING = 4
    }
}