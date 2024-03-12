package ua.leonidius.beatinspector.data.tracks.shared.domain

data class SongSearchResult(
    override val id: String,
    val name: String,
    val artists: List<Artist>,
    val isExplicit: Boolean,
    val imageUrl: String,
    val smallestImageUrl: String? = null,
): ua.leonidius.beatinspector.data.shared.domain.SearchResult
