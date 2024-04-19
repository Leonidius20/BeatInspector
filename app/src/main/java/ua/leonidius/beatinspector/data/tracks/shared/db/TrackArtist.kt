package ua.leonidius.beatinspector.data.tracks.shared.db

import androidx.room.Entity

/**
 * represent the relationship between a track and 1 or more
 * artists who made it.
 */
@Entity(tableName = "track_artists", primaryKeys = ["trackId", "artistId"])
data class TrackArtist(
    val trackId: String,
    val artistId: String,
)
