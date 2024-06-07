package ua.leonidius.beatinspector.data.shared.network.dto

import androidx.annotation.Keep

/**
 * Spotify "ImageObject"
 */
@Keep
data class ImageDto(
    val url: String,
    val height: Int,
    val width: Int,
)