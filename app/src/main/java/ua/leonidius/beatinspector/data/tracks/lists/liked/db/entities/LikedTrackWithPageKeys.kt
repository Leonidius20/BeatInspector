package ua.leonidius.beatinspector.data.tracks.lists.liked.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("liked_tracks")
data class LikedTrackWithPageKeys(
    @PrimaryKey val trackId: String,

    @ColumnInfo(name = "prev_key") val prevKey: Int?,
    @ColumnInfo(name = "next_key") val nextKey: Int?,

    @ColumnInfo(name = "cached_at")
    val cachedAt: Long = System.currentTimeMillis()
)