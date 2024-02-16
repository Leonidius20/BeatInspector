package ua.leonidius.beatinspector.datasources.network.dto

import com.google.gson.annotations.SerializedName

data class SavedTracksResponse(

    @SerializedName("previous")
    val previousUrl: String?,

    @SerializedName("next")
    val nextUrl: String?,

    @SerializedName("total")
    val numberOfItems: Int,

    val offset: Int, // same as in request
    val limit: Int,

    val items: List<SavedTrackDto>
)