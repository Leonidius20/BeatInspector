package ua.leonidius.beatinspector.data.tracks.search.network.dto

import androidx.annotation.Keep
import ua.leonidius.beatinspector.data.shared.ListMapper
import ua.leonidius.beatinspector.data.shared.Mapper
import ua.leonidius.beatinspector.data.tracks.shared.domain.SongSearchResult
import ua.leonidius.beatinspector.data.tracks.shared.network.dto.TrackDto

@Keep
data class SearchResultsResponse(
    val tracks: Tracks
): Mapper<List<SongSearchResult>>, ListMapper<SongSearchResult> {

    @Keep
    data class Tracks(
        val total: Int,
        val items: List<TrackDto>
    )

    override fun toDomainObject(): List<SongSearchResult> {
        return tracks.items.map { it.toDomainObject() }
    }

}
