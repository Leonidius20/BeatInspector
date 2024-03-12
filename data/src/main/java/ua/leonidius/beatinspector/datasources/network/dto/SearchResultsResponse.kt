package ua.leonidius.beatinspector.datasources.network.dto

import ua.leonidius.beatinspector.datasources.network.mappers.Mapper
import ua.leonidius.beatinspector.datasources.network.mappers.toDomainObject
import ua.leonidius.beatinspector.data.tracks.shared.domain.SongSearchResult

data class SearchResultsResponse(
    val tracks: Tracks
): Mapper<List<ua.leonidius.beatinspector.data.tracks.shared.domain.SongSearchResult>> {

    data class Tracks(
        val total: Int,
        val items: List<TrackDto>
    )

    override fun toDomainObject(): List<ua.leonidius.beatinspector.data.tracks.shared.domain.SongSearchResult> {
        return tracks.items.map { it.toDomainObject() }
    }

}
