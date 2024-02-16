package ua.leonidius.beatinspector.datasources.network.dto

import com.google.gson.annotations.SerializedName

data class AccountInfoResponse(
    val id: String,

    @SerializedName("display_name")
    val displayName: String,

    val images: List<ImageDto>
) {
    val smallestImage: ImageDto?
        get() = images.minByOrNull { it.height * it.width }

    val biggestImage : ImageDto?
        get() = images.maxByOrNull { it.height * it.width }

}