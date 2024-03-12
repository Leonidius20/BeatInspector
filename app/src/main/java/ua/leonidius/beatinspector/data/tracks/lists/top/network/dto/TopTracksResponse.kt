package ua.leonidius.beatinspector.data.tracks.lists.top.network.dto

import ua.leonidius.beatinspector.data.tracks.shared.network.dto.TrackDto
import ua.leonidius.beatinspector.datasources.network.mappers.toDomainObject

data class TopTracksResponse(
    val items: List<TrackDto>
): ua.leonidius.beatinspector.data.shared.ListMapper<ua.leonidius.beatinspector.data.tracks.shared.domain.SongSearchResult> {

    override fun toDomainObject(): List<ua.leonidius.beatinspector.data.tracks.shared.domain.SongSearchResult> {
        return items.map { it.toDomainObject() }
    }

}
