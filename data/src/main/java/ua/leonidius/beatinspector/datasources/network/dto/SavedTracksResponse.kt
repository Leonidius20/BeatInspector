package ua.leonidius.beatinspector.datasources.network.dto

import com.google.gson.annotations.SerializedName
import ua.leonidius.beatinspector.datasources.network.mappers.ListMapper
import ua.leonidius.beatinspector.entities.SongSearchResult

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
): ListMapper<SongSearchResult> {

    override fun toDomainObject(): List<SongSearchResult> {
        return items.map { it.toDomainObject() }
    }

}