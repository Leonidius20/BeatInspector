package ua.leonidius.beatinspector.datasources.network.dto

import ua.leonidius.beatinspector.datasources.network.mappers.Mapper
import ua.leonidius.beatinspector.data.tracks.shared.domain.Artist

/**
 * This does not include genres and is returned in search results and whatnot.
 */
data class ArtistDto(
    val id: String,
    val name: String,
): Mapper<ua.leonidius.beatinspector.data.tracks.shared.domain.Artist> {

    override fun toDomainObject(): ua.leonidius.beatinspector.data.tracks.shared.domain.Artist {
        return ua.leonidius.beatinspector.data.tracks.shared.domain.Artist(
            id = id,
            name = name,
        )
    }

}