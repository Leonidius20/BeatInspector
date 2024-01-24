package ua.leonidius.beatinspector.repos

import com.haroldadmin.cnradapter.NetworkResponse
import ua.leonidius.beatinspector.SongDataIOException
import ua.leonidius.beatinspector.entities.Song
import ua.leonidius.beatinspector.entities.SongSearchResult
import ua.leonidius.beatinspector.repos.datasources.SongsNetworkDataSource
import ua.leonidius.beatinspector.repos.datasources.SongsInMemCache
import ua.leonidius.beatinspector.repos.retrofit.SpotifyRetrofitClient

class SongsRepositoryImpl(
    private val spotifyRetrofitClient: SpotifyRetrofitClient,
    private val inMemCache: SongsInMemCache,
    private val networkDataSource: SongsNetworkDataSource
) : SongsRepository {

    companion object { // todo better solution


        fun <T, R> handleResponseOrThrow(
            result: NetworkResponse<T, SpotifyRetrofitClient.SpotifyError>,
            onSuccess: (NetworkResponse.Success<T, SpotifyRetrofitClient.SpotifyError>) -> R
        ): R {
            when (result) {
                is NetworkResponse.Success -> {
                    return onSuccess(result)
                }

                is NetworkResponse.ServerError -> {
                    throw SongDataIOException(
                        SongDataIOException.Type.SERVER,
                        result.body?.message,
                        result.error
                    )
                }

                is NetworkResponse.NetworkError -> {
                    throw SongDataIOException(
                        SongDataIOException.Type.NETWORK,
                        result.error.message,
                        result.error.cause
                    )
                }

                is NetworkResponse.UnknownError -> {
                    throw SongDataIOException(
                        SongDataIOException.Type.UNKNOWN,
                        result.error.message,
                        result.error.cause
                    )
                }

                is NetworkResponse.Error -> {
                    throw SongDataIOException(
                        SongDataIOException.Type.OTHER,
                        result.body?.message,
                        result.error
                    )
                }
            }
        }

    }

    override suspend fun searchForSongsByTitle(q: String): List<SongSearchResult> {
        return handleResponseOrThrow(spotifyRetrofitClient.searchForSongs(q)) { successResp ->
            successResp.body.tracks.items.map {
                SongSearchResult(it.id, it.name, it.artistsListToString(), it.album.images[0].url)
            }.onEach { inMemCache.songSearchResults[it.id] = it }
        }
    }

    class NotAuthedError: Error()


    override suspend fun getTrackDetails(id: String): Song {
        val baseInfo = inMemCache.songSearchResults[id]
            ?: throw Error("no base info found in cache for song id $id")

        var details = inMemCache.getSongDetailsById(id)

        if (details == null) {
            details = networkDataSource.getSongDetailsById(id)
            if (details == null) {
                throw Error("no details could be loaded from network for song id $id")
            } else {
                inMemCache.songsDetails[id] = details
            }
        }

        return Song(
            id = baseInfo.id,
            name = baseInfo.name,
            artist = baseInfo.artist,
            duration = details.duration,
            loudness = details.loudness,
            bpm = details.bpm,
            bpmConfidence = details.bpmConfidence,
            timeSignature = details.timeSignature,
            timeSignatureConfidence = details.timeSignatureConfidence,
            key = details.key,
            keyConfidence = details.keyConfidence,
            modeConfidence = details.modeConfidence,
            genres = details.genres,
            albumArtUrl = baseInfo.imageUrl,
        )
    }

}