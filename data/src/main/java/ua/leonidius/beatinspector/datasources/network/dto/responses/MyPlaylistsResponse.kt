package ua.leonidius.beatinspector.datasources.network.dto.responses

import ua.leonidius.beatinspector.datasources.network.dto.SimplifiedPlaylistDto
import ua.leonidius.beatinspector.datasources.network.mappers.ListMapper
import ua.leonidius.beatinspector.datasources.network.mappers.Mapper
import ua.leonidius.beatinspector.data.playlists.domain.PlaylistSearchResult

data class MyPlaylistsResponse(
    val items: List<SimplifiedPlaylistDto>
): Mapper<List<ua.leonidius.beatinspector.data.playlists.domain.PlaylistSearchResult>>, ListMapper<ua.leonidius.beatinspector.data.playlists.domain.PlaylistSearchResult> {

    override fun toDomainObject(): List<ua.leonidius.beatinspector.data.playlists.domain.PlaylistSearchResult> {
        return items.map { it.toDomainObject() }
    }

}