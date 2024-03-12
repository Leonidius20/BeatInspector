package ua.leonidius.beatinspector.data.tracks.lists.playlist.network.dto

import ua.leonidius.beatinspector.data.tracks.shared.network.dto.AlbumDto
import ua.leonidius.beatinspector.data.tracks.shared.network.dto.ArtistDto
import ua.leonidius.beatinspector.data.tracks.shared.network.dto.TrackDto
import ua.leonidius.beatinspector.datasources.network.mappers.toDomainObject

data class PlaylistResponse(
    val items: List<PlaylistTrackDto>
): ua.leonidius.beatinspector.data.shared.ListMapper<ua.leonidius.beatinspector.data.tracks.shared.domain.SongSearchResult> {

    data class PlaylistTrackDto(
        val track: PlaylistTrackOrPodcastEpDto
    ) {

        data class PlaylistTrackOrPodcastEpDto(
            val id: String,
            val name: String,
            val type: String,              // "track" or "episode"
            val artists: List<ArtistDto>?, // null if this is a podcast episode
            val album: AlbumDto?,          // null if this is a podcast episode
            val explicit: Boolean,
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
            album = it.track.album!!,
            explicit = it.track.explicit,
        )
    }

    override fun toDomainObject(): List<ua.leonidius.beatinspector.data.tracks.shared.domain.SongSearchResult> {
        return onlyTracks().map {
            it.toDomainObject()
        }
    }

}


