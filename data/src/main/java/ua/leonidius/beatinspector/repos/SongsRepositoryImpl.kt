package ua.leonidius.beatinspector.repos

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ua.leonidius.beatinspector.domain.entities.SongDetails
import ua.leonidius.beatinspector.domain.entities.SongSearchResult
import ua.leonidius.beatinspector.domain.repositories.SongsRepository
import ua.leonidius.beatinspector.repos.retrofit.SpotifyRetrofitClient
import ua.leonidius.beatinspector.repos.retrofit.SpotifySearchResult


class SongsRepositoryImpl(
    val spotifyRetrofitClient: SpotifyRetrofitClient
) : SongsRepository {

    override suspend fun searchForSongsByTitle(q: String): List<SongSearchResult> {
        val result = spotifyRetrofitClient.searchForSongs(q)
        if (!result.isSuccessful) {
            throw Error("error when doing the request" + result.errorBody()!!.string())
            // todo: proper error handling
        } else {
            if (result.code() == 418) {
                // no stored token found
                throw NotAuthedError()
            } else {
                return result.body()!!.tracks.items.map {
                    SongSearchResult(it.id, it.name, it.artistsListToString())
                }
            }


        }
    }

    class NotAuthedError: Error()


    override suspend fun getTrackDetails(id: String): SongDetails {
        val result = spotifyRetrofitClient.getTrackDetails(id)

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