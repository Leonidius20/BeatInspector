package ua.leonidius.beatinspector.data.tracks.shared.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * This is the "shelf" data of the track i.e. the data that the user
 * sees first, including the name and the cover art. The list of artists
 * is JOIN-ed in SQL.
 */
@Entity(tableName = "tracks")
data class TrackBaseDetails(

    @PrimaryKey val trackId: String,

    val name: String,

    @ColumnInfo(name = "is_explicit") val isExplicit: Boolean,

    @ColumnInfo(name = "big_image_url") val imageUrl: String,

    @ColumnInfo(name = "small_image_url") val smallestImageUrl: String? = null,

    val artistNames: String, // separated by comma with space

    val artistIds: String, // separated by comma
)