package ua.leonidius.beatinspector.datasources.network.dto

/**
 * This does not include genres and is returned in search results and whatnot.
 */
data class ArtistDto(
    val id: String,
    val name: String,
)