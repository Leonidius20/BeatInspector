package ua.leonidius.beatinspector.data.tracks.lists.liked.network.dto

import com.google.gson.annotations.SerializedName
import ua.leonidius.beatinspector.datasources.network.mappers.toDomainObject
import ua.leonidius.beatinspector.data.tracks.shared.network.dto.TrackDto

data class LikedTrackDto(

    @SerializedName("added_at")
    val addedAtDateString: String, // we can sort these strings and that would be equivalent to sorting by date

    val track: TrackDto,
): ua.leonidius.beatinspector.data.shared.Mapper<ua.leonidius.beatinspector.data.tracks.shared.domain.SongSearchResult> {

    override fun toDomainObject(): ua.leonidius.beatinspector.data.tracks.shared.domain.SongSearchResult {
        return track.toDomainObject()
    }

}