package ua.leonidius.beatinspector.entities

data class PlaylistSearchResult(
    override val id: String,
    val name: String,
    val smallImageUrl: String?,
    val bigImageUrl: String?,
    val uri: String,
): SearchResult