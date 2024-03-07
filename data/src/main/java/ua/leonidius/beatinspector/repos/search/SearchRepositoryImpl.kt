package ua.leonidius.beatinspector.repos.search

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.leonidius.beatinspector.datasources.cache.SongTitlesInMemCache
import ua.leonidius.beatinspector.datasources.network.SearchNetworkDataSource
import ua.leonidius.beatinspector.entities.SongSearchResult

class SearchRepositoryImpl(

    private val ioDispatcher: CoroutineDispatcher,

    private val properNetworkDataSource: SearchNetworkDataSource,
    private val searchCacheDataSource: SongTitlesInMemCache,
    private val hideExplicit: Flow<Boolean>,
) : SearchRepository {

    // we don't save query results in cache, because they are cached by okhttp

    override suspend fun get(q: String): List<SongSearchResult> = withContext(ioDispatcher) {
        var results = properNetworkDataSource.load(q)

        if (hideExplicit.first()) {
            results = results.filter { !it.isExplicit }
        }

        launch {
            searchCacheDataSource.batchAdd(results.associateBy { it.id })
        }
        results
    }

    override fun getById(id: String): SongSearchResult {
        return searchCacheDataSource[id]
            ?: throw Error("no base info found in cache for song id $id")
        // todo maybe add network call here if not found in cache
    }

    class NotAuthedError: Error() // todo: find a place for this

}