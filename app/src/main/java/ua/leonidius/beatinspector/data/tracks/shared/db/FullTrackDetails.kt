package ua.leonidius.beatinspector.data.tracks.shared.db

import androidx.room.Embedded
import androidx.room.Relation

data class FullTrackDetails(
    @Embedded
    val trackBaseInfo: TrackBaseDetails,

    @Relation(
        parentColumn = "trackId",
        entityColumn = "trackId",
    )
    val trackAudioAndGenreDetails: TrackExtendedDetails,
)
