package ua.leonidius.beatinspector.data.tracks.shared.db

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import ua.leonidius.beatinspector.data.tracks.shared.domain.Artist

data class TrackShelfInfoWithArtists(
    @Embedded val trackShelfInfo: TrackShelfInfo,

    @Relation(
        associateBy = Junction(TrackArtist::class),
        parentColumn = "trackId",
        entityColumn = "artistId",
    )
    val artists: List<Artist>
)