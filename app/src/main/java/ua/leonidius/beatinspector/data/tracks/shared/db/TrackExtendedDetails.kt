package ua.leonidius.beatinspector.data.tracks.shared.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * track audio analysis details (bpm, key, so on)
 */
@Entity(tableName = "track_audio_details", foreignKeys = [
    ForeignKey(
        entity = TrackBaseDetails::class,
        parentColumns = arrayOf("trackId"),
        childColumns = arrayOf("trackId"),
        onUpdate = ForeignKey.CASCADE,
        onDelete = ForeignKey.CASCADE,
    )
])
data class TrackExtendedDetails(
    @PrimaryKey
    val trackId: String,

    val duration: Double,
    val loudness: Double,
    val bpm: Double,
    val bpmConfidence: Double,
    val timeSignature: Int, // over 4
    val timeSignatureConfidence: Double,
    val key: String, // todo: enum
    val keyConfidence: Double,
    val modeConfidence: Double,

    val genres: String, // just a list of genres separated by comma with space

)