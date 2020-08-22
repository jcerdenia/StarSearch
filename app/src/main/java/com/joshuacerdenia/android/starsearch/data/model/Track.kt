package com.joshuacerdenia.android.starsearch.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Track(
    @PrimaryKey
    @SerializedName("trackId", alternate = ["collectionId"])
    val id: String,
    @SerializedName("trackName")
    val name: String? = null,
    @SerializedName("collectionName")
    val album: String? = null,
    @SerializedName("artistName")
    val artist: String? = null,
    @SerializedName("artworkUrl100")
    val artwork: String? = null,
    @SerializedName("trackPrice", alternate = ["collectionPrice"])
    val price: Float? = null,
    @SerializedName("primaryGenreName")
    val genre: String? = null,
    @SerializedName("longDescription", alternate = ["shortDescription"])
    val description: String? = null,
    @SerializedName("trackViewUrl", alternate = ["collectionViewUrl"])
    val url: String? = null
)