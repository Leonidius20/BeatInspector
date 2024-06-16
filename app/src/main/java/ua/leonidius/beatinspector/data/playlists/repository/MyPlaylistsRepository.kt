package ua.leonidius.beatinspector.data.playlists.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ua.leonidius.beatinspector.data.playlists.domain.PlaylistSearchResult
import ua.leonidius.beatinspector.data.playlists.network.api.MyPlaylistsService
import ua.leonidius.beatinspector.data.shared.db.TracksDatabase
import ua.leonidius.beatinspector.shared.logic.eventbus.EventBus
import ua.leonidius.beatinspector.shared.logic.eventbus.UserLogoutRequestEvent
import javax.inject.Inject

private const val PAGE_SIZE = 30

class MyPlaylistsRepository @Inject constructor(
    private val api: MyPlaylistsService,
    private val db: TracksDatabase,
    eventBus: EventBus,
) {

    // here we will have method to load new data which will empty the db table
    // and load new data there. The shit should be


    init {
        // make sure playlists cache is cleared after logging out,
        // bc these playlists belong to the user that is logging out
        eventBus.subscribe(UserLogoutRequestEvent::class) {
            db.playlistPageKeysDao().clearKeys()
            db.playlistDao().clearAll()
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    fun getMyPlaylistsFlow(): Flow<PagingData<PlaylistSearchResult>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            remoteMediator = MyPlaylistsMediator(api, db),
            pagingSourceFactory = { db.playlistDao().getAll() }
        ).flow
    }

}