package ua.leonidius.beatinspector.data.tracks.shared.network.dto

import ua.leonidius.beatinspector.data.shared.network.dto.ImageDto

data class AlbumDto(
    val id: String,
    val images: List<ImageDto> // first image is the biggest one
) {

    fun smallestImageUrl() = images.minByOrNull { it.width * it.height }?.url

    fun biggestImageUrl() = images.firstOrNull()?.url

}