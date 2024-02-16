package ua.leonidius.beatinspector.datasources.network.dto

data class AlbumDto(
    val id: String,
    val images: List<ImageDto> // first image is the biggest one
)