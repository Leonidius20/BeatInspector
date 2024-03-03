package ua.leonidius.beatinspector.entities

data class SongSearchResult(
    override val id: String,
    val name: String,
    val artists: List<Artist>,
    val isExplicit: Boolean,
    val imageUrl: String,
    val smallestImageUrl: String? = null,
): SearchResult
