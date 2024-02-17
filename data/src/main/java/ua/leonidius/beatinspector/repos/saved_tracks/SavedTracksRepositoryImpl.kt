package ua.leonidius.beatinspector.repos.saved_tracks

import ua.leonidius.beatinspector.datasources.cache.SearchCacheDataSource
import ua.leonidius.beatinspector.datasources.network.SavedTracksNetworkDataSource
import ua.leonidius.beatinspector.entities.SongSearchResult

class SavedTracksRepositoryImpl(
    private val networkDataSource: SavedTracksNetworkDataSource,
    private val searchCache: SearchCacheDataSource,
): SavedTracksRepository {
    override suspend fun get(): List<SongSearchResult> {
        return networkDataSource.get().also { searchCache.updateCache("", it) } // todo caching the list
    }

}