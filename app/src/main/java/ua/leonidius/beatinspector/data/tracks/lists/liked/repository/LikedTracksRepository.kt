package ua.leonidius.beatinspector.data.tracks.lists.liked.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ua.leonidius.beatinspector.data.tracks.lists.liked.LikedTracksNetworkPagingSource
import ua.leonidius.beatinspector.data.tracks.lists.liked.db.daos.LikedTracksDao
import ua.leonidius.beatinspector.data.tracks.shared.domain.SongSearchResult
import ua.leonidius.beatinspector.shared.logic.eventbus.EventBus
import ua.leonidius.beatinspector.shared.logic.eventbus.UserLogoutRequestEvent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LikedTracksRepository @Inject constructor(
    eventBus: EventBus,
    private val mediator: LikedTracksRemoteMediator,
    private val likedTracksDao: LikedTracksDao,
) {

    init {
        eventBus.subscribe(UserLogoutRequestEvent::class) {
            // todo: clear cache
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    fun getLikedTracksPagedFlow(): Flow<PagingData<SongSearchResult>> {
        return Pager(
            config = PagingConfig(
                pageSize = LikedTracksNetworkPagingSource.ITEMS_PER_PAGE,
                enablePlaceholders = false,
            ),
            remoteMediator = mediator,
            pagingSourceFactory = {
                likedTracksDao.likedTracksPagingSource()
            }
        ).flow.map { data ->
            data.map { item ->
                item.toDomainObject()
            }
        }
    }


}