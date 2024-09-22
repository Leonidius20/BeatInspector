package ua.leonidius.beatinspector.data.tracks.shared.db

import androidx.room.Entity
import androidx.room.ForeignKey

/**
 * represent the relationship between a track and 1 or more
 * artists who made it.
 */
@Entity(
    tableName = "track_artists",
    primaryKeys = ["trackId", "artistId"],
    foreignKeys = [
        ForeignKey(
            entity = TrackShelfInfo::class,
            parentColumns = arrayOf("trackId"),
            childColumns = arrayOf("trackId"),
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = Artist::class,
            parentColumns = arrayOf("artistId"),
            childColumns = arrayOf("artistId"),
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE,
        )
    ]
)
data class TrackArtistAssociation(
    val trackId: String,
    val artistId: String,
)
