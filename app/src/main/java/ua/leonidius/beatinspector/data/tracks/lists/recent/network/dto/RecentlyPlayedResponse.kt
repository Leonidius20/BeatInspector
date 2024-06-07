package ua.leonidius.beatinspector.data.tracks.lists.recent.network.dto

import androidx.annotation.Keep
import ua.leonidius.beatinspector.data.shared.ListMapper
import ua.leonidius.beatinspector.data.tracks.shared.domain.SongSearchResult
import ua.leonidius.beatinspector.data.tracks.shared.network.dto.TrackDto

@Keep
data class RecentlyPlayedResponse(
    val cursors: Cursors?,
    val items: List<PlayHistoryDto>,
): ListMapper<SongSearchResult> {

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