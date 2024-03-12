package ua.leonidius.beatinspector.datasources.network.dto.responses

import ua.leonidius.beatinspector.datasources.network.dto.TrackDto
import ua.leonidius.beatinspector.datasources.network.mappers.ListMapper
import ua.leonidius.beatinspector.datasources.network.mappers.toDomainObject
import ua.leonidius.beatinspector.data.tracks.shared.domain.SongSearchResult

data class TopTracksResponse(
    val items: List<TrackDto>
): ListMapper<ua.leonidius.beatinspector.data.tracks.shared.domain.SongSearchResult> {

    override fun toDomainObject(): List<ua.leonidius.beatinspector.data.tracks.shared.domain.SongSearchResult> {
        return items.map { it.toDomainObject() }
    }

}
