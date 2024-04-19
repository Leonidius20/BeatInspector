package ua.leonidius.beatinspector.data.tracks.shared.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "artists")
data class Artist(

    @PrimaryKey val id: String,

    val name: String,
)