package ua.leonidius.beatinspector.data.tracks.shared.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ua.leonidius.beatinspector.data.tracks.shared.domain.SongSearchResult

@Dao
interface TrackDao {

    @Insert
    fun insertAll(tracks: List<SongSearchResult>)

    @Query("DELETE FROM tracks")
    fun clearAll()

    @Query("SELECT * FROM tracks LIMIT :limit OFFSET :offset")
    fun getPaginated(limit: Int, offset: Int): List<SongSearchResult>

}