package ua.leonidius.beatinspector.data.tracks.details.network.dto

import androidx.annotation.Keep

/**
 * An artist DTO that includes all info, including genres. Spotify search API does
 * not return genres in the artist objects, this is way there are two Artist DTOs
 */
@Keep
data class FullArtistDto(
    val id: String,
    val name: String,
    val genres: List<String>
)