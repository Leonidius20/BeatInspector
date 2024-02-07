package ua.leonidius.beatinspector.repos.datasources

import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import ua.leonidius.beatinspector.entities.Artist
import ua.leonidius.beatinspector.entities.SongDetails
import ua.leonidius.beatinspector.services.SpotifyRetrofitClient
import ua.leonidius.beatinspector.toUIException

class SongsNetworkDataSourceImpl(
    private val spotifyRetrofitClient: SpotifyRetrofitClient,
    private val ioDispatcher: CoroutineDispatcher
) : SongsNetworkDataSource {

    override suspend fun getTrackAudioAnalysis(
        trackId: String, artists: List<Artist>
    ): SongDetails = withContext(ioDispatcher) {

        val trackAnalysisDeferredResponse = async {
            when (val response = spotifyRetrofitClient.getTrackAudioAnalysis(trackId)) {
                is NetworkResponse.Success -> Result.success(response.body.track)

                is NetworkResponse.Error -> Result.failure(response.toUIException())
            }
        }

        val genresDeferredResponse = async {
            when (val response = spotifyRetrofitClient.getArtists(artists.joinToString(",") { it.id })) {
                is NetworkResponse.Success -> Result.success(
                    response.body.artists.map { it.genres }.flatten().distinct())

                is NetworkResponse.Error -> Result.failure(response.toUIException())
                // ??? throw response.toUIException()
            }
        }

        val trackAnalysis = trackAnalysisDeferredResponse.await().getOrThrow()

        // if genres request fails, we still want to show the song details, so we just show no genres
        val genres = genresDeferredResponse.await().getOrDefault(emptyList())

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