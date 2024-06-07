package ua.leonidius.beatinspector.data.tracks.lists.liked.network.dto

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import ua.leonidius.beatinspector.data.shared.Mapper
import ua.leonidius.beatinspector.data.tracks.shared.domain.SongSearchResult
import ua.leonidius.beatinspector.data.tracks.shared.network.dto.TrackDto

@Keep
data class LikedTrackDto(

    @SerializedName("added_at")
    val addedAtDateString: String, // we can sort these strings and that would be equivalent to sorting by date

    val track: TrackDto,
): Mapper<SongSearchResult> {

    override fun toDomainObject(): ua.leonidius.beatinspector.data.tracks.shared.domain.SongSearchResult {
        return track.toDomainObject()
    }

}