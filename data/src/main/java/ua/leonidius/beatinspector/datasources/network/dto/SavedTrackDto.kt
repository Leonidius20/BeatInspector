package ua.leonidius.beatinspector.datasources.network.dto

import com.google.gson.annotations.SerializedName

data class SavedTrackDto(

    @SerializedName("added_at")
    val addedAtDateString: String, // we can sort these strings and that would be equivalent to sorting by date

    val track: TrackDto,
)