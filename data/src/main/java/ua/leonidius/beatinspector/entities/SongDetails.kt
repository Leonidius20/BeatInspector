package ua.leonidius.beatinspector.entities

data class SongDetails(
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



    // todo: image cover
) {

    companion object {
        val Dummy = SongDetails(
            duration = 0.0,
            loudness = 0.0,
            bpm = 0.0,
            bpmConfidence = 0.0,
            timeSignature = 0,
            timeSignatureConfidence = 0.0,
            key = "",
            keyConfidence = 0.0,
            modeConfidence = 0.0,
        )
    }

}