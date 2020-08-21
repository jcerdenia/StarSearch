package com.joshuacerdenia.android.starsearch.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.joshuacerdenia.android.starsearch.data.TrackFetcher
import com.joshuacerdenia.android.starsearch.data.model.Track

class TrackListViewModel: ViewModel() {

    private val trackFetcher = TrackFetcher.get()
    val tracksLiveData: LiveData<List<Track>> = trackFetcher.getTracks()

    fun updateCache(tracks: List<Track>) {
        trackFetcher.updateCache(tracks)
    }
}