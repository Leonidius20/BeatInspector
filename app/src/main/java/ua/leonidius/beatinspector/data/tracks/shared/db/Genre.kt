package ua.leonidius.beatinspector.data.tracks.shared.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "genres")
data class Genre(
    @PrimaryKey(autoGenerate = true) val genreId: Int = 0, // 0 means let SQLite generate the value
    val name: String,
)