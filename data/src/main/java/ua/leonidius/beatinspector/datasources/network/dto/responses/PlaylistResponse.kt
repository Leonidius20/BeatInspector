package ua.leonidius.beatinspector.datasources.network.dto.responses

import ua.leonidius.beatinspector.datasources.network.dto.AlbumDto
import ua.leonidius.beatinspector.datasources.network.dto.ArtistDto
import ua.leonidius.beatinspector.datasources.network.dto.TrackDto
import ua.leonidius.beatinspector.datasources.network.mappers.ListMapper
import ua.leonidius.beatinspector.datasources.network.mappers.toDomainObject
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
            val type: String,              // "track" or "episode"
            val artists: List<ArtistDto>?, // null if this is a podcast episode
            val album: AlbumDto?           // null if this is a podcast episode
        ) {
            fun isTrack() = type == "track"
        }

        fun isTrack() = track.isTrack()

    }

    private fun onlyTracks() = items.filter { it.isTrack() }.map {
        TrackDto(
            id = it.track.id,
            name = it.track.name,
            artists = it.track.artists!!,
            album = it.track.album!!
        )
    }

    override fun toDomainObject(): List<SongSearchResult> {
        return onlyTracks().map {
            it.toDomainObject()
        }
    }

}


