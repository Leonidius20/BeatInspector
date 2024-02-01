package ua.leonidius.beatinspector.repos.datasources

import android.util.Log
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import ua.leonidius.beatinspector.SongDataIOException
import ua.leonidius.beatinspector.entities.Artist
import ua.leonidius.beatinspector.entities.SongDetails
import ua.leonidius.beatinspector.repos.retrofit.SpotifyRetrofitClient

class SongsNetworkDataSourceImpl(
    private val spotifyRetrofitClient: SpotifyRetrofitClient,
    private val ioDispatcher: CoroutineDispatcher
): SongsNetworkDataSource {

    override suspend fun getSongDetailsById(
        trackId: String, artists: List<Artist>
    ): Pair<SongDetails, List<String>> = withContext(ioDispatcher) {

        val deferredGenres = artists.asSequence().map { artist ->
            async {
                when (val response = spotifyRetrofitClient.getArtist(artist.id)) {
                    is NetworkResponse.Success -> Pair(artist, response.body.genres)
                    else -> Pair(artist, null)
                }
            }

        }.toList().toTypedArray()

       val artistsAndGenreLists = awaitAll(*deferredGenres) // .filterNotNull().flatten().distinct().toList()

        val (succeededArtists, failedArtists) = artistsAndGenreLists.partition { it.second != null }


       val failedArtistsNames = failedArtists.map { it.first.name }

        val genres = succeededArtists.map { it.second!! }.flatten().distinct().toList()

       val trackDetails = when (val response = spotifyRetrofitClient.getTrackDetails(trackId)) {
           is NetworkResponse.Success -> response.body.track

           is NetworkResponse.ServerError -> {
               throw SongDataIOException.Server(response.code, response.body?.message ?: "< No response body >")
           }
           is NetworkResponse.NetworkError -> {
                throw SongDataIOException.Network(response.error)
           }
           is NetworkResponse.UnknownError -> {
               throw SongDataIOException.Unknown(response.error)
           }
       }

       Log.d("SongsNetworkDataSource", "Failed artists: $failedArtistsNames")

       with(trackDetails) {
           return@withContext Pair(SongDetails(
               duration,
               loudness,
               tempo,
               tempoConfidence,
               timeSignature,
               timeSignatureConfidence,
               getKeyStringFromSpotifyValue(key, mode),
               keyConfidence,
               modeConfidence,
               genres
           ), failedArtistsNames)
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