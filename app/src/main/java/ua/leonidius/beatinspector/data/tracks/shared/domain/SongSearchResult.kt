package ua.leonidius.beatinspector.data.tracks.shared.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracks")
data class SongSearchResult(
    @PrimaryKey override val id: String,
    val name: String,

    val artists: List<Artist>,
    @ColumnInfo(name = "is_explicit") val isExplicit: Boolean,
    @ColumnInfo(name = "big_image_url") val imageUrl: String,
    @ColumnInfo(name = "small_image_url") val smallestImageUrl: String? = null,
): ua.leonidius.beatinspector.data.shared.domain.SearchResult
