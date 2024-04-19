package ua.leonidius.beatinspector.data.tracks.shared.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface TrackDao {

    @Transaction
    @Query("SELECT * FROM tracks WHERE id = :trackId")
    fun getTrackShelfInfoWithArtists(trackId: String): List<TrackShelfInfoWithArtists>

}