package ua.leonidius.beatinspector.repos

import android.util.Log
import com.haroldadmin.cnradapter.NetworkResponse
import ua.leonidius.beatinspector.SongDataIOException
import ua.leonidius.beatinspector.entities.Artist
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
        Log.d("SongsRepository", "searchForSongsByTitle: q = $q")
        return handleResponseOrThrow(spotifyRetrofitClient.searchForSongs(q)) { successResp ->
            successResp.body.tracks.items.map {
                // todo: make data mappers a separate thing
                SongSearchResult(it.id, it.name, it.artists.map { Artist(it.id, it.name) }, it.album.images[0].url)
            }.onEach { inMemCache.songSearchResults[it.id] = it }
        }
    }

    class NotAuthedError: Error()


    /**
     * @return Pair of song details and list of artists that failed to get their genres
     */
    override suspend fun getTrackDetails(id: String): Pair<Song, List<String>> {
        val baseInfo = inMemCache.songSearchResults[id]
            ?: throw Error("no base info found in cache for song id $id")

        var details = inMemCache.getSongDetailsById(id)
        var failedArtists = emptyList<String>()

        if (details == null) {
            val result = networkDataSource.getSongDetailsById(id, baseInfo.artists)
            details = result.first
            failedArtists = result.second
            inMemCache.songsDetails[id] = details
        }

        return Pair(Song(
            id = baseInfo.id,
            name = baseInfo.name,
            artist = baseInfo.artists.joinToString(", ") { it.name }, // todo: don't, just return as is and let ui layer handle it
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
        ), failedArtists)
    }

}