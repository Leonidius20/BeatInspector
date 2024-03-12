package ua.leonidius.beatinspector.data.playlists.domain

import ua.leonidius.beatinspector.data.shared.domain.SearchResult

data class PlaylistSearchResult(
    override val id: String,
    val name: String,
    val smallImageUrl: String?,
    val bigImageUrl: String?,
    val uri: String,
): SearchResult