package ua.leonidius.beatinspector.repos.datasources

import android.util.Log
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import ua.leonidius.beatinspector.entities.SongDetails
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
                spotifyRetrofitClient.getTrack(trackId).body()!!.artists.forEach {
                    deferred.add(async { spotifyRetrofitClient.getArtist(it.id).body()!! })
                }
                deferred.map { it.await() }.flatMap { it.genres  }.distinct()
            }
            val detailsRespDef = async { spotifyRetrofitClient.getTrackDetails(trackId) }

            Log.d("SongsNetworkDataSource", "starting requests")


            val genres = genresDef.await()
            val detailsResp = detailsRespDef.await()

            Log.d("SongsNetworkDataSource", "requests finished")
            //Log.d("SongsNetworkDataSource", "trackResp: " + trackResp.body().toString())
            Log.d("SongsNetworkDataSource", "detailsResp: " + detailsResp.body().toString())

            if (!detailsResp.isSuccessful) {
                Log.e("SongsNetworkDataSource", "error when doing the request" + detailsResp.errorBody()!!.string())
                throw Error("error when doing the request" + detailsResp.errorBody()!!.string())
                // todo: proper error handling
               // } else if (!trackResp.isSuccessful) {
                //     Log.e("SongsNetworkDataSource", "error when doing the request" + trackResp.errorBody()!!.string())
                //     throw Error("error when doing the request" + trackResp.errorBody()!!.string())
            } else {
                with(detailsResp.body()!!.track) {
                    SongDetails(
                        duration,
                        loudness,
                        tempo,
                        tempoConfidence,
                        timeSignature,
                        timeSignatureConfidence,
                        getKeyStringFromSpotifyValue(key, mode),
                        keyConfidence,
                        modeConfidence,
                        //trackResp.body()!!.artists.flatMap { it.genres }
                        genres
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