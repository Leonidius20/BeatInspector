package ua.leonidius.beatinspector.data.tracks.lists.recent.network.dto

import ua.leonidius.beatinspector.data.tracks.shared.network.dto.TrackDto
import ua.leonidius.beatinspector.datasources.network.mappers.toDomainObject

data class RecentlyPlayedResponse(
    val cursors: Cursors?,
    val items: List<PlayHistoryDto>,
): ua.leonidius.beatinspector.data.shared.ListMapper<ua.leonidius.beatinspector.data.tracks.shared.domain.SongSearchResult> {

    data class Cursors(
        val after: String,
        val before: String,
    )

    data class PlayHistoryDto(
        val track: TrackDto,
    ): ua.leonidius.beatinspector.data.shared.Mapper<ua.leonidius.beatinspector.data.tracks.shared.domain.SongSearchResult> {

        override fun toDomainObject() = track.toDomainObject()

    }

    override fun toDomainObject() = items.map { it.track.toDomainObject() }

}