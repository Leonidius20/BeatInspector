package ua.leonidius.beatinspector.datasources.network.dto

data class TrackDto(
    val id: String,
    val name: String,
    val album: AlbumDto,
    val artists: List<ArtistDto>,
    val explicit: Boolean,
) {

    fun artistsListToString() = artists.map { it.name }.joinToString(", ")

}