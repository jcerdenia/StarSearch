package com.joshuacerdenia.android.starsearch.data

import android.content.Context

object TrackPreferences {

    private const val TRACK_PREFS = "TRACK_PREFS"
    private const val KEY_ORDER = "KEY_ORDER"
    private const val KEY_LAST_VIEWED_PAGE = "KEY_LAST_VIEWED_PAGE"
    private const val KEY_DATE_LAST_VIEWED = "KEY_DATE_LAST_VIEWED"
    private const val KEY_TRACK_ID = "KEY_TRACK_ID"

    fun getStoredOrder(context: Context): Int {
        val trackPrefs = context.getSharedPreferences(TRACK_PREFS, Context.MODE_PRIVATE)
        return trackPrefs.getInt(KEY_ORDER, 0)
    }

    fun saveOrder(context: Context, order: Int) {
        val trackPrefs = context.getSharedPreferences(TRACK_PREFS, Context.MODE_PRIVATE)
        trackPrefs.edit().putInt(KEY_ORDER, order).apply()
    }

    fun getLastViewedPage(context: Context): Int {
        val trackPrefs = context.getSharedPreferences(TRACK_PREFS, Context.MODE_PRIVATE)
        return trackPrefs.getInt(KEY_LAST_VIEWED_PAGE, 0)
    }

    fun savePage(context: Context, page: Int) {
        val trackPrefs = context.getSharedPreferences(TRACK_PREFS, Context.MODE_PRIVATE)
        trackPrefs.edit().putInt(KEY_LAST_VIEWED_PAGE, page).apply()
    }

    fun getLastViewedTrackId(context: Context): String? {
        val trackPrefs = context.getSharedPreferences(TRACK_PREFS, Context.MODE_PRIVATE)
        return trackPrefs.getString(KEY_TRACK_ID, null)
    }

    fun saveTrackId(context: Context, trackId: String) {
        val trackPrefs = context.getSharedPreferences(TRACK_PREFS, Context.MODE_PRIVATE)
        trackPrefs.edit().putString(KEY_TRACK_ID, trackId).apply()
    }

    fun getDateLastViewed(context: Context): String? {
        val trackPrefs = context.getSharedPreferences(TRACK_PREFS, Context.MODE_PRIVATE)
        return trackPrefs.getString(KEY_DATE_LAST_VIEWED, null)
    }

    fun saveDate(context: Context, date: String) {
        val trackPrefs = context.getSharedPreferences(TRACK_PREFS, Context.MODE_PRIVATE)
        trackPrefs.edit().putString(KEY_DATE_LAST_VIEWED, date).apply()
    }
}