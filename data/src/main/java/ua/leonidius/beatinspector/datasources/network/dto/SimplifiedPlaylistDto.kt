package ua.leonidius.beatinspector.datasources.network.dto

import ua.leonidius.beatinspector.datasources.network.mappers.Mapper
import ua.leonidius.beatinspector.data.playlists.domain.PlaylistSearchResult

data class SimplifiedPlaylistDto(
    val id: String,
    val name: String,
    val images: List<ImageDto>,
    val uri: String,
): Mapper<ua.leonidius.beatinspector.data.playlists.domain.PlaylistSearchResult> {

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