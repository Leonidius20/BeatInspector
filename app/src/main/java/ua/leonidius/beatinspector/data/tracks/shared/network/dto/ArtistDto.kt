package ua.leonidius.beatinspector.data.tracks.shared.network.dto

import ua.leonidius.beatinspector.data.shared.Mapper
import ua.leonidius.beatinspector.data.tracks.shared.domain.Artist

/**
 * This does not include genres and is returned in search results and whatnot.
 */
data class ArtistDto(
    val id: String,
    val name: String,
): ua.leonidius.beatinspector.data.shared.Mapper<ua.leonidius.beatinspector.data.tracks.shared.domain.Artist> {

    override fun toDomainObject(): ua.leonidius.beatinspector.data.tracks.shared.domain.Artist {
        return ua.leonidius.beatinspector.data.tracks.shared.domain.Artist(
            id = id,
            name = name,
        )
    }

}