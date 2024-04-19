package ua.leonidius.beatinspector.data.playlists.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ua.leonidius.beatinspector.data.playlists.domain.PlaylistSearchResult

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(playlists: List<PlaylistSearchResult>)

    @Query("DELETE FROM playlists")
    suspend fun clearAll()

    @Query("SELECT * FROM playlists")
    fun getAll(): PagingSource<Int, PlaylistSearchResult>

}