package ua.leonidius.beatinspector.data.tracks.shared.db

import androidx.room.Entity
import androidx.room.ForeignKey

/**
 * represents that an artist is associated with (0 or more) genres.
 */
@Entity(
    tableName = "artist_genres",
    primaryKeys = ["artistId", "genreId"],
    foreignKeys = [
        ForeignKey(
            entity = Artist::class,
            parentColumns = arrayOf("artistId"),
            childColumns = arrayOf("artistId"),
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = Genre::class,
            parentColumns = arrayOf("genreId"),
            childColumns = arrayOf("genreId"),
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE,
        ),
    ]
)
data class ArtistGenreAssociation(
    val artistId: String,
    val genreId: Int,
)