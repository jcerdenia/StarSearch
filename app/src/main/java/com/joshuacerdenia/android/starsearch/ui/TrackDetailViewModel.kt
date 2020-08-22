package com.joshuacerdenia.android.starsearch.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.joshuacerdenia.android.starsearch.PAGE_TRACK_DETAIL
import com.joshuacerdenia.android.starsearch.data.TrackPreferences
import com.joshuacerdenia.android.starsearch.data.TrackRepository
import com.joshuacerdenia.android.starsearch.data.model.Track

class TrackDetailViewModel(private val app: Application): AndroidViewModel(app) {

    private val repository = TrackRepository.getInstance()
    //val storedTrackId = TrackPreferences.getLastViewedTrackId(app)
    var dateLastViewed = TrackPreferences.getDateLastViewed(app)
        private set

    private val trackIdLiveData = MutableLiveData<String>()
    val trackLiveData: LiveData<Track?> = Transformations.switchMap(trackIdLiveData) { id ->
        repository.getTrackDetailsById(id)
    }

    fun getTrackById(id: String) {
        trackIdLiveData.value = id
    }
}