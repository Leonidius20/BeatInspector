package ua.leonidius.beatinspector.data.tracks.shared.network.dto

import ua.leonidius.beatinspector.data.shared.Mapper
import ua.leonidius.beatinspector.data.tracks.shared.domain.SongSearchResult

data class TrackDto(
    val id: String,
    val name: String,
    val album: AlbumDto,
    val artists: List<ArtistDto>,
    val explicit: Boolean,
): Mapper<SongSearchResult> {

    fun artistsListToString() = artists.map { it.name }.joinToString(", ")

    override fun toDomainObject(): SongSearchResult {
        val name = if (this.explicit) "$name \uD83C\uDD74" else name // "E" emoji
        return SongSearchResult(
            id = id,
            name = name,
            artists = artists.map { it.toDomainObject() },
            isExplicit = explicit,
            imageUrl = album.images[0].url,
            smallestImageUrl = album.images.minByOrNull { it.width * it.height }?.url,
        )
    }

}