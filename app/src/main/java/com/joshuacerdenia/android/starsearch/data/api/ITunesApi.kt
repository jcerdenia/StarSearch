package com.joshuacerdenia.android.starsearch.data.api

import retrofit2.Call
import retrofit2.http.GET

interface ITunesApi {

    @GET("search?term=star&amp;country=au&amp;media=movie&amp;all")
    fun fetchTracks(): Call<ITunesResponse>
}