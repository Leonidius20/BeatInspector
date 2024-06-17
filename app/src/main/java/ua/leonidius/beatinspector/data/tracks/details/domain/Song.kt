package ua.leonidius.beatinspector.data.tracks.details.domain

data class Song(
    val id: String,
    val name: String,
    val artist: String,

    val duration: Double,
    val loudness: Double,
    val bpm: Double,
    val bpmConfidence: Double,
    val timeSignature: Int, // over 4
    val timeSignatureConfidence: Double,
    val key: String, // todo: enum
    val keyConfidence: Double,
    val modeConfidence: Double,
    val genres: List<String> = listOf(),
    val albumArtUrl: String?,
)
