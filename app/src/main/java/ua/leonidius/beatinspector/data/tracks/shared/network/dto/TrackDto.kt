package ua.leonidius.beatinspector.data.tracks.shared.network.dto

import androidx.annotation.Keep
import ua.leonidius.beatinspector.data.shared.Mapper
import ua.leonidius.beatinspector.data.tracks.shared.domain.SongSearchResult

@Keep
data class TrackDto(
    val id: String,
    val name: String,
    val album: AlbumDto,
    val artists: List<ArtistDto>,
    val explicit: Boolean,
): Mapper<SongSearchResult> {

    override fun toDomainObject(): SongSearchResult {
        // val name = if (this.explicit) "$name \uD83C\uDD74" else name // "E" emoji
        return SongSearchResult(
            id = id,
            name = name,
            artistNames = artists.joinToString(", ") { it.name },
            artistIds = artists.joinToString(",") { it.id },
            isExplicit = explicit,
            imageUrl = album.biggestImageUrlOrNull(),
            smallestImageUrl = album.smallestImageUrlOrNull(),
        )
    }

}