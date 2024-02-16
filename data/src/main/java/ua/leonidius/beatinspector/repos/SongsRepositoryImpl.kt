package ua.leonidius.beatinspector.repos

import android.util.Log
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ua.leonidius.beatinspector.datasources.cache.SearchCacheDataSource
import ua.leonidius.beatinspector.datasources.network.SearchNetworkDataSource
import ua.leonidius.beatinspector.datasources.network.services.SearchService
import ua.leonidius.beatinspector.entities.Artist
import ua.leonidius.beatinspector.entities.Song
import ua.leonidius.beatinspector.entities.SongSearchResult
import ua.leonidius.beatinspector.repos.datasources.SongsInMemCache
import ua.leonidius.beatinspector.repos.datasources.SongsNetworkDataSource
import ua.leonidius.beatinspector.datasources.network.services.SpotifyRetrofitClient
import ua.leonidius.beatinspector.toUIException

class SongsRepositoryImpl(
    // private val spotifyRetrofitClient: SpotifyRetrofitClient,
    private val searchService: SearchService, // todo: replace with NetworkDataSource
    private val inMemCache: SongsInMemCache,

    private val ioDispatcher: CoroutineDispatcher,
    private val networkDataSource: SongsNetworkDataSource,

    private val cachedDataSource: SearchCacheDataSource,
) : SongsRepository {

    // todo: only combine these flows in the UI layer? or, alternatively,
    // create 2 separate flows - 1 for results, and one for error messages,
    // and have the UI show result + error message, e.g. cached result
    // and a message about how refresh failed


    override val resultsFlow = cachedDataSource.resultsFlow

    override suspend fun search(q: String) {
        cachedDataSource.load(q)
    }

    override suspend fun searchForSongsByTitle(q: String): List<SongSearchResult> = withContext(ioDispatcher) {
        Log.d("SongsRepository", "searchForSongsByTitle: q = $q")

        val result = searchService.search(q) // may throw SongDataIOException.TokenRefresh

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
            is NetworkResponse.Error -> {
                throw result.toUIException()
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