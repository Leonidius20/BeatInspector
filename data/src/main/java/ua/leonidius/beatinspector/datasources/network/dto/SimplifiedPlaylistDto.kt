package ua.leonidius.beatinspector.datasources.network.dto

import ua.leonidius.beatinspector.datasources.network.mappers.Mapper
import ua.leonidius.beatinspector.entities.PlaylistSearchResult

data class SimplifiedPlaylistDto(
    val id: String,
    val name: String,
    val images: List<ImageDto>,
    val uri: String,
): Mapper<PlaylistSearchResult> {

    private val smallestImageOrNull: ImageDto?
        get() = images.lastOrNull() // according to the API, the smallest image is the last one

    override fun toDomainObject(): PlaylistSearchResult {
        return PlaylistSearchResult(
            id = id,
            name = name,
            smallImageUrl = smallestImageOrNull?.url,
            uri = uri,
        )
    }

}