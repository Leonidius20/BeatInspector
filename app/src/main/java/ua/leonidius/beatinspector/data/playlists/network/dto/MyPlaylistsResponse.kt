package ua.leonidius.beatinspector.data.playlists.network.dto

import androidx.annotation.Keep
import ua.leonidius.beatinspector.data.playlists.domain.PlaylistSearchResult
import ua.leonidius.beatinspector.data.shared.ListMapper
import ua.leonidius.beatinspector.data.shared.Mapper

@Keep
data class MyPlaylistsResponse(
    val items: List<SimplifiedPlaylistDto>
): Mapper<List<PlaylistSearchResult>>,
    ListMapper<PlaylistSearchResult> {

    override fun toDomainObject(): List<PlaylistSearchResult> {
        return items.map { it.toDomainObject() }
    }

}