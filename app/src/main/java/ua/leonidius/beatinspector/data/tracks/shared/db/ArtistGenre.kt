package ua.leonidius.beatinspector.data.tracks.shared.db

import androidx.room.Entity

/**
 * represents that an artist is associated with (0 or more) genres.
 * it is possible that artist (his id and name) are cached, but the genres where
 * not. This is why this is a separate table.
 */
@Entity(tableName = "artist_genres", primaryKeys = ["artistId", "genre"])
data class ArtistGenre(
    val artistId: String,
    val genre: String
)