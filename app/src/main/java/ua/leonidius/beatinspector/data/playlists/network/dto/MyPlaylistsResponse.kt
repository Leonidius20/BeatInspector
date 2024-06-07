package ua.leonidius.beatinspector.data.playlists.network.dto

import androidx.annotation.Keep

@Keep
data class MyPlaylistsResponse(
    val items: List<SimplifiedPlaylistDto>
): ua.leonidius.beatinspector.data.shared.Mapper<List<ua.leonidius.beatinspector.data.playlists.domain.PlaylistSearchResult>>,
    ua.leonidius.beatinspector.data.shared.ListMapper<ua.leonidius.beatinspector.data.playlists.domain.PlaylistSearchResult> {

    override fun toDomainObject(): List<ua.leonidius.beatinspector.data.playlists.domain.PlaylistSearchResult> {
        return items.map { it.toDomainObject() }
    }

}