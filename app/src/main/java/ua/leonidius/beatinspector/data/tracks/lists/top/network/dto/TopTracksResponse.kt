package ua.leonidius.beatinspector.data.tracks.lists.top.network.dto

import ua.leonidius.beatinspector.data.shared.ListMapper
import ua.leonidius.beatinspector.data.tracks.shared.domain.SongSearchResult
import ua.leonidius.beatinspector.data.tracks.shared.network.dto.TrackDto

data class TopTracksResponse(
    val items: List<TrackDto>
): ListMapper<SongSearchResult> {

    override fun toDomainObject(): List<SongSearchResult> {
        return items.map { it.toDomainObject() }
    }

}
