package ua.leonidius.beatinspector.repos.datasources

import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import ua.leonidius.beatinspector.SongDataIOException
import ua.leonidius.beatinspector.entities.Artist
import ua.leonidius.beatinspector.entities.SongDetails
import ua.leonidius.beatinspector.repos.retrofit.SpotifyRetrofitClient
import ua.leonidius.beatinspector.repos.retrofit.SpotifyTrackAnalysisResponse

class SongsNetworkDataSourceImpl(
    private val spotifyRetrofitClient: SpotifyRetrofitClient
): SongsNetworkDataSource {
    override suspend fun getSongDetailsById(trackId: String, artists: List<Artist>): SongDetails {



        // new version
       return withContext(Dispatchers.IO) { // todo: remove hardcoded dispatcher
            val deferredGenres = artists.asSequence().map { it.id }.map { artistId ->
                async {
                    when (val response = spotifyRetrofitClient.getArtist(artistId)) {
                        is NetworkResponse.Success -> response.body.genres
                        else -> null// todo: notify the user about the error, but don't throw an exception. Maybe make a Map<Artist, List<Genre>?> and from there getseparately the flat list of genres and the list of errored out artists
                    }
                }

            }.toList().toTypedArray()

           val genres = awaitAll(*deferredGenres).filterNotNull().flatten().distinct().toList()

           val trackDetails = when (val response = spotifyRetrofitClient.getTrackDetails(trackId)) {
               is NetworkResponse.Success -> response.body.track
               else -> throw SongDataIOException(badResponseClassToType(response::class.java))
           }

           with(trackDetails) {
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
                   genres
               )
           }
        }



        // old version:

        // todo: fix this mess using flows? or something else?
        //val trackResp = spotifyRetrofitClient.getTrack(trackId)
           /* return coroutineScope {
            val genresDef = async {
                val deferred = mutableListOf<Deferred<SpotifyRetrofitClient.ArtistResponse>>()

                // todo: error handling for the one getTrack() request (what does it even get, isn't this cached?) as well as for all artist requests

                (spotifyRetrofitClient.getTrack(trackId) as NetworkResponse.Success).body.artists.forEach {
                    deferred.add(async { (spotifyRetrofitClient.getArtist(it.id) as NetworkResponse.Success).body })
                }
                deferred.map {
                    //try {
                        it.await()
                    //} catch (e: Exception) {
                    //    Log.d("SongsNetworkDataSource", "error when doing the request for artist genre" + e.message)
                    //    null
                    //}
                }.flatMap { resp -> resp.genres }/*.filter { it != null }*/.distinct()
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
        }*/
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

    fun badResponseClassToType(badResponseClass: Class<out NetworkResponse<SpotifyTrackAnalysisResponse, SpotifyRetrofitClient.SpotifyError>>): SongDataIOException.Type {
        return when(badResponseClass) {
            NetworkResponse.ServerError::class.java -> SongDataIOException.Type.SERVER
            NetworkResponse.NetworkError::class.java -> SongDataIOException.Type.NETWORK
            NetworkResponse.UnknownError::class.java -> SongDataIOException.Type.UNKNOWN
            NetworkResponse.Error::class.java -> SongDataIOException.Type.OTHER
            else -> SongDataIOException.Type.OTHER
        }
    }

}