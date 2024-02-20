package ua.leonidius.beatinspector.datasources.network.dto.responses

import ua.leonidius.beatinspector.datasources.network.dto.SimplifiedPlaylistDto
import ua.leonidius.beatinspector.datasources.network.mappers.ListMapper
import ua.leonidius.beatinspector.datasources.network.mappers.Mapper
import ua.leonidius.beatinspector.entities.PlaylistSearchResult

data class MyPlaylistsResponse(
    val items: List<SimplifiedPlaylistDto>
): Mapper<List<PlaylistSearchResult>>, ListMapper<PlaylistSearchResult> {

    override fun toDomainObject(): List<PlaylistSearchResult> {
        return items.map { it.toDomainObject() }
    }

}