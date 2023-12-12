package ua.leonidius.beatinspector.repos.retrofit

import com.google.gson.annotations.SerializedName

data class SpotifyTrackAnalysisResponse(
    val track: TrackResponse,
) {

    data class TrackResponse(
        val duration: Double,
        val loudness: Double, // db but which kind of db? between -60 and 0
        val tempo: Double,
        @SerializedName("tempo_confidence")
        val tempoConfidence: Double, // from 0.0 to 1.0


        @SerializedName("time_signature")
        val timeSignature: Int, /* An estimated time signature. The time signature (meter) is a notational convention to specify how many beats are in each bar (or measure). The time signature ranges from 3 to 7 indicating time signatures of "3/4", to "7/4".
                                Range: 3 - 7
                                Example: 4 */

        @SerializedName("time_signature_confidence")
        val timeSignatureConfidence: Double,

        val key: Int, /* The key the track is in. Integers map to pitches using standard Pitch Class notation. E.g. 0 = C, 1 = C♯/D♭, 2 = D, and so on. If no key was detected, the value is -1.
                        Range: -1 - 11
                        Example: 9
                         https://en.wikipedia.org/wiki/Pitch_class
                         */

        @SerializedName("key_confidence")
        val keyConfidence: Double,

        val mode: Int, // major =1, minor = 0

        @SerializedName("mode_confidence")
        val modeConfidence: Double,


        @SerializedName("rhythmstring")
        val rhythmString: String, // todo: learn what this is and if it is useful to analyse percussion patterns

        @SerializedName("rhythm_version")
        val rhythmVersion: Double,


        // todo: display by sections ("sections")
        // val sections: List<Section>,
    )

}
