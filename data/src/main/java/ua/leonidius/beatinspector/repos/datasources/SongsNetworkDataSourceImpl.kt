package ua.leonidius.beatinspector.repos.datasources

import ua.leonidius.beatinspector.entities.SongDetails
import ua.leonidius.beatinspector.repos.retrofit.SpotifyRetrofitClient

class SongsNetworkDataSourceImpl(
    private val spotifyRetrofitClient: SpotifyRetrofitClient
): SongsNetworkDataSource {
    override suspend fun getSongDetailsById(trackId: String): SongDetails {
        val result = spotifyRetrofitClient.getTrackDetails(trackId)

        if (!result.isSuccessful) {
            throw Error("error when doing the request" + result.errorBody()!!.string())
            // todo: proper error handling
        } else {
            with(result.body()!!.track) {
                return SongDetails(
                    duration,
                    loudness,
                    tempo,
                    tempoConfidence,
                    timeSignature,
                    timeSignatureConfidence,
                    getKeyStringFromSpotifyValue(key, mode),
                    keyConfidence,
                    modeConfidence,
                )
            }
        }
    }

    private fun getKeyStringFromSpotifyValue(keyInt: Int, modeInt: Int): String {
        val key = when(keyInt) {
            0 -> "C"
            1 -> "C♯/D♭"
            2 -> "D"
            3 -> "D♯/E♭"
            4 -> "E"
            5 -> "F"
            6 -> "F♯/G♭"
            7 -> "G"
            8 -> "G♯/A♭"
            9 -> "A"
            10 -> "A♯/B♭"
            11 -> "B"
            else -> "?"
        }

        val mode = when(modeInt) {
            1 -> "Maj"
            0 -> "Min"
            else -> "?"

        }

        return "$key $mode"
    }

}