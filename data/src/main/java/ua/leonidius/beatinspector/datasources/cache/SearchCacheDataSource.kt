package ua.leonidius.beatinspector.datasources.cache

import ua.leonidius.beatinspector.entities.SongSearchResult

/**
 *
 */
class SearchCacheDataSource {


    //private val inMemCache = mutableMapOf<String, List<SongSearchResult>>()
    private val idToTitleCache = mutableMapOf<String, SongSearchResult>()


    fun updateCache(q: String, results: List<SongSearchResult>) {
        // inMemCache[q] = results
        results.forEach { idToTitleCache[it.id] = it }
    }

    fun getTitleInfo(id: String): SongSearchResult? {
        return idToTitleCache[id]
    }


}