package ua.leonidius.beatinspector.data.tracks.shared.network.dto

import androidx.annotation.Keep
import ua.leonidius.beatinspector.data.shared.Mapper
import ua.leonidius.beatinspector.data.tracks.shared.domain.Artist

/**
 * This does not include genres and is returned in search results and whatnot.
 */
@Keep
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