package ua.leonidius.beatinspector.entities

data class SongSearchResult(
    val id: String,
    val name: String,
    val artists: List<Artist>,
    val imageUrl: String,
)
