package ua.leonidius.beatinspector.repos.datasources

import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import ua.leonidius.beatinspector.SongDataIOException
import ua.leonidius.beatinspector.entities.Artist
import ua.leonidius.beatinspector.entities.SongDetails
import ua.leonidius.beatinspector.services.SpotifyRetrofitClient

class SongsNetworkDataSourceImpl(
    private val spotifyRetrofitClient: SpotifyRetrofitClient,
    private val ioDispatcher: CoroutineDispatcher
) : SongsNetworkDataSource {

    override suspend fun getTrackAudioAnalysis(
        trackId: String, artists: List<Artist>
    ): SongDetails = withContext(ioDispatcher) {

        val trackAnalysisDeferredResponse = async {
            spotifyRetrofitClient.getTrackAudioAnalysis(trackId)
        }

        val genresDeferredResponse = async {
            spotifyRetrofitClient.getArtists(artists.joinToString(",") { it.id })
        }

        val trackAnalysis = when (val response = trackAnalysisDeferredResponse.await()) {
            is NetworkResponse.Success -> response.body.track

            is NetworkResponse.ServerError -> {
                throw SongDataIOException.Server(
                    response.code,
                    response.body?.message ?: "< No response body >"
                )
            }

            is NetworkResponse.NetworkError -> {
                throw SongDataIOException.Network(response.error)
            }

            is NetworkResponse.UnknownError -> {
                throw SongDataIOException.Unknown(response.error)
            }
        }

        val genres = when (val response = genresDeferredResponse.await()) {
            is NetworkResponse.Success -> {
                response.body.artists.map { it.genres }.flatten().distinct()
            }
            is NetworkResponse.ServerError -> {
                throw SongDataIOException.Server(
                    response.code,
                    response.body?.message ?: "< No response body >"
                )
            }

            is NetworkResponse.NetworkError -> {
                throw SongDataIOException.Network(response.error)
            }

            is NetworkResponse.UnknownError -> {
                throw SongDataIOException.Unknown(response.error)
            }
        }

        // todo: make data mappers a separate thing
        with(trackAnalysis) {
            return@withContext SongDetails(
                duration,
                loudness,
                tempo,
                tempoConfidence,
                timeSignature,
                timeSignatureConfidence,
                getKeyStringFromSpotifyValue(key, mode),
                keyConfidence,
                modeConfidence,
                genres,
            )

        }
    }

    private fun getKeyStringFromSpotifyValue(keyInt: Int, modeInt: Int): String {
        val key = when (keyInt) {
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

        val mode = when (modeInt) {
            1 -> "Maj"
            0 -> "Min"
            else -> "?"

        }

        return "$key $mode"
    }

}