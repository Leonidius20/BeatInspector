package ua.leonidius.beatinspector.data.tracks.shared.network.dto

import androidx.annotation.Keep
import ua.leonidius.beatinspector.data.shared.network.dto.ImageDto

@Keep
data class AlbumDto(
    val id: String,
    val images: List<ImageDto>? // first image is the biggest one
) {

    fun smallestImageUrlOrNull() = images?.minByOrNull { it.width * it.height }?.url

    fun biggestImageUrlOrNull() = images?.firstOrNull()?.url

}