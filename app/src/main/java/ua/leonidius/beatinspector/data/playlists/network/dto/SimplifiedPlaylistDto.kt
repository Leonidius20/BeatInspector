package ua.leonidius.beatinspector.data.playlists.network.dto

import androidx.annotation.Keep
import ua.leonidius.beatinspector.data.shared.network.dto.ImageDto

@Keep
data class SimplifiedPlaylistDto(
    val id: String,
    val name: String,
    val images: List<ImageDto>,
    val uri: String,
): ua.leonidius.beatinspector.data.shared.Mapper<ua.leonidius.beatinspector.data.playlists.domain.PlaylistSearchResult> {

    private val smallestImageOrNull: ImageDto?
        get() = images.lastOrNull() // according to the API, the smallest image is the last one

    private val biggestImageOrNull: ImageDto?
        get() = images.firstOrNull() // according to the API, images returned in descending order

    override fun toDomainObject(): ua.leonidius.beatinspector.data.playlists.domain.PlaylistSearchResult {
        return ua.leonidius.beatinspector.data.playlists.domain.PlaylistSearchResult(
            id = id,
            name = name,
            smallImageUrl = smallestImageOrNull?.url,
            bigImageUrl = biggestImageOrNull?.url,
            uri = uri,
        )
    }

}