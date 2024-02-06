package ua.leonidius.beatinspector.repos

import android.util.Log
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ua.leonidius.beatinspector.SongDataIOException
import ua.leonidius.beatinspector.entities.Artist
import ua.leonidius.beatinspector.entities.Song
import ua.leonidius.beatinspector.entities.SongSearchResult
import ua.leonidius.beatinspector.repos.datasources.SongsNetworkDataSource
import ua.leonidius.beatinspector.repos.datasources.SongsInMemCache
import ua.leonidius.beatinspector.services.SpotifyRetrofitClient

class SongsRepositoryImpl(
    private val spotifyRetrofitClient: SpotifyRetrofitClient,
    private val inMemCache: SongsInMemCache,
    private val networkDataSource: SongsNetworkDataSource,
    private val ioDispatcher: CoroutineDispatcher,
) : SongsRepository {

    override suspend fun searchForSongsByTitle(q: String): List<SongSearchResult> = withContext(ioDispatcher) {
        Log.d("SongsRepository", "searchForSongsByTitle: q = $q")

        val result = spotifyRetrofitClient.searchForSongs(q) // may throw SongDataIOException.TokenRefresh

        when(result) {
            is NetworkResponse.Success -> {
                return@withContext result.body.tracks.items.map {
                    // todo: make data mappers a separate thing
                    SongSearchResult(
                        id = it.id,
                        name = it.name,
                        artists = it.artists.map { Artist(it.id, it.name) },
                        imageUrl = it.album.images[0].url,
                    )
                }.onEach { inMemCache.songSearchResults[it.id] = it }
            }
            is NetworkResponse.ServerError -> {
                throw SongDataIOException.Server(result.code, result.body?.message ?: "< No response body >")
            }
            is NetworkResponse.NetworkError -> {
                throw SongDataIOException.Network(result.error)
            }
            is NetworkResponse.UnknownError -> {
                throw SongDataIOException.Unknown(result.error)
            }
        }
    }

    class NotAuthedError: Error()


    /**
     * @return Pair of song details and list of artists that failed to get their genres
     */
    override suspend fun getTrackDetails(id: String): Song = withContext(ioDispatcher) {
        val baseInfo = inMemCache.songSearchResults[id]
            ?: throw Error("no base info found in cache for song id $id")

        var details = inMemCache.getSongDetailsById(id)

        if (details == null) {
            details = networkDataSource.getTrackAudioAnalysis(id, baseInfo.artists) // may throw SongDataIOException
            inMemCache.songsDetails[id] = details
        }

        return@withContext Song(
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
        )

    }

}