package ua.leonidius.beatinspector.data.tracks.lists.liked.db.daos

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ua.leonidius.beatinspector.data.tracks.lists.liked.db.entities.LikedTrackWithPageKeys
import ua.leonidius.beatinspector.data.tracks.shared.db.TrackBaseDetails

@Dao
interface LikedTracksDao {

    @Query("select * from liked_tracks join tracks using(trackId)")
    fun likedTracksPagingSource(): PagingSource<Int, TrackBaseDetails>

    @Query("delete from liked_tracks")
    suspend fun clearAll()

    @Insert
    suspend fun insertAll(likedTracks: List<LikedTrackWithPageKeys>)

    @Query("select * from liked_tracks where trackId = :id")
    suspend fun getById(id: String): LikedTrackWithPageKeys

    @Query("SELECT cached_at FROM liked_tracks ORDER BY cached_at DESC LIMIT 1")
    suspend fun getCachingTimestamp(): Long?

}