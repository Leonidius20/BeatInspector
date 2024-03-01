package ua.leonidius.beatinspector.datasources.network.dto

import ua.leonidius.beatinspector.datasources.network.mappers.Mapper
import ua.leonidius.beatinspector.entities.Artist

/**
 * This does not include genres and is returned in search results and whatnot.
 */
data class ArtistDto(
    val id: String,
    val name: String,
): Mapper<Artist> {

    override fun toDomainObject(): Artist {
        return Artist(
            id = id,
            name = name,
        )
    }

}