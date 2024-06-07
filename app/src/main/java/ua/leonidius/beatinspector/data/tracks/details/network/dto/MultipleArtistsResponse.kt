package ua.leonidius.beatinspector.data.tracks.details.network.dto

import androidx.annotation.Keep

@Keep
data class MultipleArtistsResponse(
    val artists: List<FullArtistDto>
)