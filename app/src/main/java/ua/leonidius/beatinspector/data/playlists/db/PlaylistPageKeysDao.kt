package ua.leonidius.beatinspector.data.playlists.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PlaylistPageKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<PlaylistPageKeys>)

    @Query("SELECT * FROM playlist_page_keys WHERE playlist_id = :playlistId")
    suspend fun keysByPlaylistId(playlistId: String): PlaylistPageKeys?

    @Query("DELETE FROM playlist_page_keys")
    suspend fun clearKeys()

    @Query("SELECT cached_at FROM playlist_page_keys ORDER BY cached_at DESC LIMIT 1")
    suspend fun getCachingTimestamp(): Long?

}