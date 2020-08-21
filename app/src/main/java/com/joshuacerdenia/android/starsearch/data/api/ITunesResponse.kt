package com.joshuacerdenia.android.starsearch.data.api

import com.google.gson.annotations.SerializedName
import com.joshuacerdenia.android.starsearch.data.model.Track

class ITunesResponse {
    @SerializedName("results")
    lateinit var tracks: List<Track>
}