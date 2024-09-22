package ua.leonidius.beatinspector.data.tracks.shared.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "artists")
data class Artist(
    @PrimaryKey val artistId: String,
    val name: String,

    val genresLoaded: Boolean, // flag that shows weather genres for this artist were fetched and cached or not

    // genres
)