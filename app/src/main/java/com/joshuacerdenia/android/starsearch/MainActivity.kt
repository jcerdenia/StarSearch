package com.joshuacerdenia.android.starsearch

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.joshuacerdenia.android.starsearch.data.TrackPreferences
import com.joshuacerdenia.android.starsearch.ui.TrackDetailFragment
import com.joshuacerdenia.android.starsearch.ui.TrackListFragment
import java.text.DateFormat
import java.util.*

const val PAGE_TRACK_LIST = 0
const val PAGE_TRACK_DETAIL = 1

class MainActivity : AppCompatActivity(),
    TrackListFragment.Callbacks,
    TrackDetailFragment.Callbacks {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val lastViewedPage = TrackPreferences.getLastViewedPage(this)

        if (savedInstanceState == null) {
            val fragment = if (lastViewedPage == PAGE_TRACK_DETAIL) {
                TrackDetailFragment.newInstance(TrackPreferences.getLastViewedTrackId(this))
            } else {
                TrackListFragment.newInstance()
            }

            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
        }
    }

    override fun onTrackSelected(id: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, TrackDetailFragment.newInstance(id))
            .addToBackStack(null)
            .commit()
    }

    override fun onHomePressed() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, TrackListFragment.newInstance())
            .commit()
    }

    override fun onStop() {
        super.onStop()
        val date = DateFormat.getDateTimeInstance(
            DateFormat.MEDIUM,
            DateFormat.SHORT
        ).format(Date())
        TrackPreferences.saveDate(this, date)
    }
}