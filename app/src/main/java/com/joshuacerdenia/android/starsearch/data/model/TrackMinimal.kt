package com.joshuacerdenia.android.starsearch.data.model

// A light version of data class Track
// that only holds properties the list page needs
data class TrackMinimal(
    val id: String,
    val name: String?,
    val album: String?,
    val artwork: String?,
    val genre: String?,
    val price: Float?
)