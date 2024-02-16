package ua.leonidius.beatinspector.datasources.cache

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import ua.leonidius.beatinspector.datasources.network.SearchNetworkDataSource
import ua.leonidius.beatinspector.entities.SongSearchResult
import ua.leonidius.beatinspector.repos.Resource

/**
 * this is the ssot for search results
 * todo: move this code to the repository
 */
class SearchCacheDataSource(
    private val networkDs: SearchNetworkDataSource
) {

    val resultsFromNetworkFlow =  networkDs.resultsFlow.map {
        val (query, result) = it
        val cachedExists = inMemCache.containsKey(query)

        if (result.isFailure) {
            if (cachedExists) {
                Resource.ValueWithError(inMemCache[query]!!, result.exceptionOrNull()!!)
            } else {
                Resource.Error(result.exceptionOrNull()!!)
            }
        } else {
            inMemCache[query] = result.getOrNull()!!
           Resource.Success(result.getOrNull()!!)
        }
    }

    val resultsFlow = MutableSharedFlow<Resource<List<SongSearchResult>>>()

    // combine the 2 flows


    private val inMemCache = mutableMapOf<String, List<SongSearchResult>>()
    // todo: replace with a proper cache



    // todo: impl. cache expiration

    suspend fun load(query: String, ) {
        resultsFlow.emit(Resource.Loading())

        // if there is a cached result, emit it immediately
        if (inMemCache.containsKey(query)) {
            // todo: emit cache miss or cache hit
            // then in the Repo handle it
            resultsFlow.emit(Resource.Success(inMemCache[query]!!))
        }

        // refresh the cache and emit the fresh version
        //todo: but only if cached version is too old
        networkDs.load(query)
    }

}