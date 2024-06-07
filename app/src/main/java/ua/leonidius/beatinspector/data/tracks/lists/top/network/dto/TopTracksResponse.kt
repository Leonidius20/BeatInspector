package ua.leonidius.beatinspector.data.tracks.lists.top.network.dto

import androidx.annotation.Keep
import ua.leonidius.beatinspector.data.shared.ListMapper
import ua.leonidius.beatinspector.data.tracks.shared.domain.SongSearchResult
import ua.leonidius.beatinspector.data.tracks.shared.network.dto.TrackDto

@Keep
data class TopTracksResponse(
    val items: List<TrackDto>
): ListMapper<SongSearchResult> {

    override fun toDomainObject(): List<SongSearchResult> {
        return items.map { it.toDomainObject() }
    }

}
