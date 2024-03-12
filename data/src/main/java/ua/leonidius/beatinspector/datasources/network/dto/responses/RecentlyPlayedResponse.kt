package ua.leonidius.beatinspector.datasources.network.dto.responses

import ua.leonidius.beatinspector.datasources.network.dto.TrackDto
import ua.leonidius.beatinspector.datasources.network.mappers.ListMapper
import ua.leonidius.beatinspector.datasources.network.mappers.Mapper
import ua.leonidius.beatinspector.datasources.network.mappers.toDomainObject
import ua.leonidius.beatinspector.data.tracks.shared.domain.SongSearchResult

data class RecentlyPlayedResponse(
    val cursors: Cursors?,
    val items: List<PlayHistoryDto>,
): ListMapper<ua.leonidius.beatinspector.data.tracks.shared.domain.SongSearchResult> {

    data class Cursors(
        val after: String,
        val before: String,
    )

    data class PlayHistoryDto(
        val track: TrackDto,
    ): Mapper<ua.leonidius.beatinspector.data.tracks.shared.domain.SongSearchResult> {

        override fun toDomainObject() = track.toDomainObject()

    }

    override fun toDomainObject() = items.map { it.track.toDomainObject() }

}