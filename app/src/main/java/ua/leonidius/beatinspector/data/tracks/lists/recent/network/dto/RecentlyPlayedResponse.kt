package ua.leonidius.beatinspector.data.tracks.lists.recent.network.dto

import androidx.annotation.Keep
import ua.leonidius.beatinspector.data.shared.ListMapper
import ua.leonidius.beatinspector.data.shared.Mapper
import ua.leonidius.beatinspector.data.tracks.shared.domain.SongSearchResult
import ua.leonidius.beatinspector.data.tracks.shared.network.dto.TrackDto

@Keep
data class RecentlyPlayedResponse(
    val cursors: Cursors?,
    val items: List<PlayHistoryDto>,
): ListMapper<SongSearchResult> {

    @Keep
    data class Cursors(
        val after: String,
        val before: String,
    )

    @Keep
    data class PlayHistoryDto(
        val track: TrackDto,
    ): Mapper<SongSearchResult> {

        override fun toDomainObject() = track.toDomainObject()

    }

    override fun toDomainObject() = items.map { it.track.toDomainObject() }

}