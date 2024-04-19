package ua.leonidius.beatinspector.data.tracks.lists.shared.db

import androidx.room.ColumnInfo
import androidx.room.Entity

/**
 * this represents paging keys for a list of tracks that belong to
 * a playlist (or liked tracks, or top tracks, etc). All of playlists
 * are using this one table, differentiated by playlist_id. DAOs should
 * filter all actions by playlistId.
 */
@Entity(
    primaryKeys = ["playlist_id", "song_id"]
)
data class SongInPlaylistPagingKeys(
    @ColumnInfo(name = "playlist_id") val playlistId: String,
    @ColumnInfo(name = "song_id") val songId: String,

    @ColumnInfo(name = "prev_key") val prevKey: Int?,
    @ColumnInfo(name = "next_key") val nextKey: Int?,

    @ColumnInfo(name = "cached_at")
    val cachedAt: Long = System.currentTimeMillis()
)
