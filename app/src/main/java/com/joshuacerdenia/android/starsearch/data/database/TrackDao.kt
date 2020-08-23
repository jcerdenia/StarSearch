package com.joshuacerdenia.android.starsearch.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.joshuacerdenia.android.starsearch.data.model.Track
import com.joshuacerdenia.android.starsearch.data.model.TrackMinimal

@Dao
interface TrackDao {

    @Query("SELECT id, name, album, artwork, genre, price FROM Track")
    fun getTracksMinimal(): LiveData<List<TrackMinimal>>

    @Query("SELECT * FROM Track WHERE id=(:id)")
    fun getTrackById(id: String): LiveData<Track?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addTracks(tracks: List<Track>)

    @Query("DELETE FROM Track WHERE id NOT IN (:ids)")
    fun deleteTracksById(ids: List<String>)

    @Transaction
    // Add the most recent version of tracks list
    // and delete any items that are not in the new list
    fun replaceTracks(tracks: List<Track>) {
        addTracks(tracks)
        tracks.map { track -> track.id }.also { ids ->
            deleteTracksById(ids)
        }
    }
}