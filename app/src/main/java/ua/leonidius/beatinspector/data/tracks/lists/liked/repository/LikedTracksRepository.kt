package ua.leonidius.beatinspector.data.tracks.lists.liked.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ua.leonidius.beatinspector.data.tracks.lists.liked.LikedTracksNetworkPagingSource
import ua.leonidius.beatinspector.data.tracks.lists.liked.network.api.LikedTracksApi
import ua.leonidius.beatinspector.data.tracks.shared.cache.TrackBaseDetailsDbDataSource
import ua.leonidius.beatinspector.data.tracks.shared.domain.SongSearchResult
import ua.leonidius.beatinspector.shared.domain.SettingsState
import ua.leonidius.beatinspector.shared.logic.eventbus.EventBus
import ua.leonidius.beatinspector.shared.logic.eventbus.UserLogoutRequestEvent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LikedTracksRepository @Inject constructor(
    private val service: LikedTracksApi,
    private val searchCache: TrackBaseDetailsDbDataSource,
    private val settingsFlow: Flow<SettingsState>,
    eventBus: EventBus,
) {

    init {
        eventBus.subscribe(UserLogoutRequestEvent::class) {
            // todo: clear cache
        }
    }

    fun getLikedTracksPagedFlow(): Flow<PagingData<SongSearchResult>> {
        return Pager(
            config = PagingConfig(
                pageSize = LikedTracksNetworkPagingSource.ITEMS_PER_PAGE,
                enablePlaceholders = false,
            ),
            // todo: mediator, change factory to room cache output
            pagingSourceFactory = {
                LikedTracksNetworkPagingSource(
                    service = service,
                    searchCache = searchCache,
                    settingsFlow = settingsFlow,
                )
            }
        ).flow
    }


}