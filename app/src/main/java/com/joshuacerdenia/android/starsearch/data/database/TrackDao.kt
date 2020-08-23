package com.joshuacerdenia.android.starsearch.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.joshuacerdenia.android.starsearch.data.model.Track
import com.joshuacerdenia.android.starsearch.data.model.TrackMinimal

@Dao
interface TrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    // REPLACE ensures data is always up to date
    fun addTracks(tracks: List<Track>)

    @Query("SELECT id, name, album, artwork, genre, price FROM Track")
    fun getTracksMinimal(): LiveData<List<TrackMinimal>>

    @Query("SELECT id, name, album, artwork, genre, price FROM Track")
    // Needed method for repository to fetch data if web request fails
    fun getTracksMinimalSynchronously(): List<TrackMinimal>

    @Query("SELECT * FROM Track WHERE id=(:id)")
    fun getTrackById(id: String): LiveData<Track?>

    @Query("DELETE FROM Track WHERE id NOT IN (:newTrackIds)")
    fun deleteOldTracks(newTrackIds: List<String>)

    @Transaction
    // Add the most recent version of tracks list
    // and delete any items that are not in the new list
    fun replaceTracks(tracks: List<Track>) {
        addTracks(tracks)
        tracks.map { track -> track.id }.also { ids ->
            deleteOldTracks(ids)
        }
    }
}