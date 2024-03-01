package ua.leonidius.beatinspector.datasources.network.dto.responses

import ua.leonidius.beatinspector.datasources.network.dto.AlbumDto
import ua.leonidius.beatinspector.datasources.network.dto.ArtistDto
import ua.leonidius.beatinspector.datasources.network.mappers.ListMapper
import ua.leonidius.beatinspector.entities.SongSearchResult

data class PlaylistResponse(
    val items: List<PlaylistTrackDto>
): ListMapper<SongSearchResult> {

    data class PlaylistTrackDto(
        val track: PlaylistTrackOrPodcastEpDto
    ) {

        data class PlaylistTrackOrPodcastEpDto(
            val id: String,
            val name: String,
            val type: String,                     // "track" or "episode"
            val artists: List<ArtistDto>? = null, // null if this is a podcast episode
            val album: AlbumDto? = null           // null if this is a podcast episode
        ) {
            fun isTrack() = type == "track"
        }

        fun isTrack() = track.isTrack()

    }

    private fun onlyTracks() = items.filter { it.isTrack() }

    override fun toDomainObject(): List<SongSearchResult> {
        return onlyTracks().map {
            SongSearchResult(
                id = it.track.id,
                name = it.track.name,
                artists = it.track.artists?.map { it.toDomainObject() } ?: emptyList(),
                imageUrl = it.track.album?.biggestImageUrl() ?: "",
                smallestImageUrl = it.track.album?.smallestImageUrl() ?: "",
            )
        }
    }

}


