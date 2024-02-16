package ua.leonidius.beatinspector.repos

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.leonidius.beatinspector.datasources.cache.SearchCacheDataSource
import ua.leonidius.beatinspector.datasources.network.SearchNetworkDataSource
import ua.leonidius.beatinspector.entities.SongSearchResult

class SongsRepositoryImpl(

    private val ioDispatcher: CoroutineDispatcher,

    private val properNetworkDataSource: SearchNetworkDataSource,
    private val searchCacheDataSource: SearchCacheDataSource,
) : SongsRepository {

    // we don't save query results in cache, because they are cached by okhttp

    override suspend fun searchForSongsByTitle(q: String): List<SongSearchResult> = withContext(ioDispatcher) {
        val results = properNetworkDataSource.load(q)
        launch {
            searchCacheDataSource.updateCache(q, results)
        }
        results
    }

    class NotAuthedError: Error() // todo: find a place for this

}