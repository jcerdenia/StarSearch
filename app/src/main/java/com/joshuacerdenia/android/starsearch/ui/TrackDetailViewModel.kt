package com.joshuacerdenia.android.starsearch.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.joshuacerdenia.android.starsearch.data.TrackFetcher
import com.joshuacerdenia.android.starsearch.data.model.Track

class TrackDetailViewModel: ViewModel() {

    private val trackFetcher = TrackFetcher.getInstance()

    private val trackIdLiveData = MutableLiveData<String>()
    val trackLiveData: LiveData<Track?> = Transformations.switchMap(trackIdLiveData) { id ->
        trackFetcher.getTrackDetailsById(id)
    }

    fun getTrackById(id: String) {
        trackIdLiveData.value = id
    }
}