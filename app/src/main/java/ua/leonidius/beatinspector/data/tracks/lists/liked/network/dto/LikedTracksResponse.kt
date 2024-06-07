package ua.leonidius.beatinspector.data.tracks.lists.liked.network.dto

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class LikedTracksResponse(

    @SerializedName("previous")
    val previousUrl: String?,

    @SerializedName("next")
    val nextUrl: String?,

    @SerializedName("total")
    val numberOfItems: Int,

    val offset: Int, // same as in request
    val limit: Int,

    val items: List<LikedTrackDto>
): ua.leonidius.beatinspector.data.shared.ListMapper<ua.leonidius.beatinspector.data.tracks.shared.domain.SongSearchResult> {

    override fun toDomainObject(): List<ua.leonidius.beatinspector.data.tracks.shared.domain.SongSearchResult> {
        return items.map { it.toDomainObject() }
    }

}