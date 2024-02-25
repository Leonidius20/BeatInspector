package ua.leonidius.beatinspector.datasources.network.dto

import com.google.gson.annotations.SerializedName
import ua.leonidius.beatinspector.datasources.network.mappers.Mapper
import ua.leonidius.beatinspector.datasources.network.mappers.toDomainObject
import ua.leonidius.beatinspector.entities.SongSearchResult

data class SavedTrackDto(

    @SerializedName("added_at")
    val addedAtDateString: String, // we can sort these strings and that would be equivalent to sorting by date

    val track: TrackDto,
): Mapper<SongSearchResult> {

    override fun toDomainObject(): SongSearchResult {
        return track.toDomainObject()
    }

}