package ua.leonidius.beatinspector.data.tracks.search.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.leonidius.beatinspector.data.tracks.search.network.SearchNetworkDataSource
import ua.leonidius.beatinspector.data.tracks.shared.cache.SongTitlesInMemCache
import ua.leonidius.beatinspector.data.tracks.shared.domain.SongSearchResult
import ua.leonidius.beatinspector.shared.logic.settings.SettingsState
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class SearchRepositoryImpl @Inject constructor(

    @Named("io") private val ioDispatcher: CoroutineDispatcher,
    private val properNetworkDataSource: SearchNetworkDataSource,
    private val searchCacheDataSource: SongTitlesInMemCache,
    private val settingsFlow: Flow<SettingsState>,
) : SearchRepository {

    // we don't save query results in cache, because they are cached by okhttp

    override suspend fun get(q: String): List<SongSearchResult> = withContext(ioDispatcher) {
        var results = properNetworkDataSource.load(q)

        if (settingsFlow.first().hideExplicit) {
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