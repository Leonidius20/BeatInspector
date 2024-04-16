package ua.leonidius.beatinspector.data.tracks.lists.liked.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.filter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import ua.leonidius.beatinspector.data.shared.repository.BaseTrackListPagingRepository
import ua.leonidius.beatinspector.data.tracks.lists.liked.SavedTracksNetworkPagingSource
import ua.leonidius.beatinspector.data.tracks.lists.liked.network.api.LikedTracksApi
import ua.leonidius.beatinspector.data.tracks.shared.cache.SongTitlesInMemCache
import ua.leonidius.beatinspector.shared.domain.SettingsState
import javax.inject.Inject

private const val PAGE_SIZE = 50

class LikedTracksRepository @Inject constructor(
    private val api: LikedTracksApi,
    private val cache: SongTitlesInMemCache,
    private val settingsFlow: Flow<SettingsState>,
): BaseTrackListPagingRepository() {
    override protected fun createPagingSource() = SavedTracksNetworkPagingSource(
        api, cache, settingsFlow // , PAGE_SIZE
    )

    override val items = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE),
        pagingSourceFactory = { createPagingSource() }
    ).flow.map { pagingData ->
        if (settingsFlow.first().hideExplicit) {
            pagingData.filter { !it.isExplicit }
        } else {
            pagingData
        }
    }

    /*
     * the "map" call here is business logic (filtering out explicit material)
     */

}