package ua.leonidius.beatinspector.datasources.network.dto.responses

import ua.leonidius.beatinspector.datasources.network.dto.TrackDto
import ua.leonidius.beatinspector.datasources.network.mappers.ListMapper
import ua.leonidius.beatinspector.datasources.network.mappers.toDomainObject
import ua.leonidius.beatinspector.entities.SongSearchResult

data class TopTracksResponse(
    val items: List<TrackDto>
): ListMapper<SongSearchResult> {

    override fun toDomainObject(): List<SongSearchResult> {
        return items.map { it.toDomainObject() }
    }

}
