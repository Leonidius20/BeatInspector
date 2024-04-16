package ua.leonidius.beatinspector.data.playlists.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ua.leonidius.beatinspector.data.shared.domain.SearchResult

@Entity(tableName = "playlists")
data class PlaylistSearchResult(
    @PrimaryKey override val id: String,

    val name: String,

    @ColumnInfo(name = "small_image_url") val smallImageUrl: String?,

    @ColumnInfo(name = "big_image_url") val bigImageUrl: String?,

    val uri: String,
): SearchResult