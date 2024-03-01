package ua.leonidius.beatinspector.repos.search

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.leonidius.beatinspector.datasources.cache.SearchCacheDataSource
import ua.leonidius.beatinspector.datasources.network.SearchNetworkDataSource
import ua.leonidius.beatinspector.entities.SongSearchResult
import ua.leonidius.beatinspector.repos.BasicRepository

class SearchRepositoryImpl(

    private val ioDispatcher: CoroutineDispatcher,

    private val properNetworkDataSource: SearchNetworkDataSource,
    private val searchCacheDataSource: SearchCacheDataSource,
) : SearchRepository {

    // we don't save query results in cache, because they are cached by okhttp

    override suspend fun get(q: String): List<SongSearchResult> = withContext(ioDispatcher) {
        val results = properNetworkDataSource.load(q)
        launch {
            searchCacheDataSource.updateCache(q, results)
        }
        results
    }

    override fun getById(id: String): SongSearchResult {
        return searchCacheDataSource.getTitleInfo(id)
            ?: throw Error("no base info found in cache for song id $id")
        // todo maybe add network call here if not found in cache
    }

    class NotAuthedError: Error() // todo: find a place for this

}