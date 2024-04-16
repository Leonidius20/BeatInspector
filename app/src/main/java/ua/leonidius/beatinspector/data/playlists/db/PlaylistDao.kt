package ua.leonidius.beatinspector.data.playlists.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import ua.leonidius.beatinspector.data.playlists.domain.PlaylistSearchResult

@Dao
interface PlaylistDao {

    @Insert
    fun insertAll(playlists: List<PlaylistSearchResult>)

    @Query("DELETE FROM playlists")
    fun clearAll()

    @Query("SELECT * FROM playlists LIMIT :limit OFFSET :offset") // todo is this rigth?
    fun getPaginated(limit: Int, offset: Int): PagingSource<Int, PlaylistSearchResult>

}