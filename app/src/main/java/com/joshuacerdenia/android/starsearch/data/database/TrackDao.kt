package com.joshuacerdenia.android.starsearch.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.joshuacerdenia.android.starsearch.data.model.Track

@Dao
interface TrackDao {

    @Query("SELECT * FROM Track")
    fun getTracks(): LiveData<List<Track>>

    @Query("SELECT * FROM Track WHERE id=(:id)")
    fun getTrackById(id: String): LiveData<Track?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addTracks(tracks: List<Track>)

    @Query("DELETE FROM Track WHERE id NOT IN (:ids)")
    fun deleteTracksById(ids: List<String>)

    @Transaction
    fun replaceTracks(tracks: List<Track>) {
        // Add the most recent version of tracks list
        // and delete any old items that are not in the list
        addTracks(tracks)
        tracks.map { track -> track.id }.also { trackIds ->
            deleteTracksById(trackIds)
        }
    }
}