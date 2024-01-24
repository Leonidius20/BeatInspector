package ua.leonidius.beatinspector.repos.datasources

import android.util.Log
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.handleCoroutineException
import ua.leonidius.beatinspector.SongDataIOException
import ua.leonidius.beatinspector.entities.SongDetails
import ua.leonidius.beatinspector.repos.SongsRepositoryImpl
import ua.leonidius.beatinspector.repos.retrofit.SpotifyRetrofitClient

class SongsNetworkDataSourceImpl(
    private val spotifyRetrofitClient: SpotifyRetrofitClient
): SongsNetworkDataSource {
    override suspend fun getSongDetailsById(trackId: String): SongDetails {
        // todo: fix this mess using flows? or something else?
        //val trackResp = spotifyRetrofitClient.getTrack(trackId)
        return coroutineScope {
            val genresDef = async {
                val deferred = mutableListOf<Deferred<SpotifyRetrofitClient.ArtistResponse>>()

                // todo: error handling for the one getTrack() request (what does it even get, isn't this cached?) as well as for all artist requests

                (spotifyRetrofitClient.getTrack(trackId) as NetworkResponse.Success).body.artists.forEach {
                    deferred.add(async { (spotifyRetrofitClient.getArtist(it.id) as NetworkResponse.Success).body })
                }
                deferred.map { it.await() }.flatMap { it.genres  }.distinct()
            }
            val detailsRespDef = async { spotifyRetrofitClient.getTrackDetails(trackId) }

            Log.d("SongsNetworkDataSource", "starting requests")


            val genres = genresDef.await()
            val detailsResp = detailsRespDef.await()

            //Log.d("SongsNetworkDataSource", "requests finished")
            //Log.d("SongsNetworkDataSource", "trackResp: " + trackResp.body().toString())
            //Log.d("SongsNetworkDataSource", "detailsResp: " + detailsResp.body.toString())

            when (detailsResp) {
                is NetworkResponse.Success -> {
                    with(detailsResp.body.track) {
                        return@coroutineScope SongDetails(
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
                        )
                    }
                }
                is NetworkResponse.ServerError -> {
                    Log.d("SongsNetworkDataSource", "error when doing the request" + detailsResp.error?.message)
                    throw SongDataIOException(
                        SongDataIOException.Type.SERVER,
                        detailsResp.body?.message,
                        detailsResp.error
                    )
                }

                is NetworkResponse.NetworkError -> {
                    throw SongDataIOException(
                        SongDataIOException.Type.NETWORK,
                        detailsResp.error.message,
                        detailsResp.error.cause
                    )
                }

                is NetworkResponse.UnknownError -> {
                    throw SongDataIOException(
                        SongDataIOException.Type.UNKNOWN,
                        detailsResp.error.message,
                        detailsResp.error.cause
                    )
                }

                is NetworkResponse.Error -> {
                    throw SongDataIOException(
                        SongDataIOException.Type.OTHER,
                        detailsResp.body?.message,
                        detailsResp.error
                    )
                }
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