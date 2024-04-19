package ua.leonidius.beatinspector.data.playlists.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.haroldadmin.cnradapter.NetworkResponse
import retrofit2.HttpException
import ua.leonidius.beatinspector.data.playlists.db.PlaylistPageKeys
import ua.leonidius.beatinspector.data.playlists.domain.PlaylistSearchResult
import ua.leonidius.beatinspector.data.playlists.network.api.MyPlaylistsService
import ua.leonidius.beatinspector.data.shared.db.TracksDatabase
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * by "synthetic" i mean that the spotify api doesn't take page numbers,
 * instead it takes offsets and limits, but we pretend to work with pages
 */
private const val START_SYNTHETIC_PAGE_INDEX = 0

@OptIn(ExperimentalPagingApi::class)
internal class MyPlaylistsMediator(
    private val api: MyPlaylistsService,
    private val db: TracksDatabase,
): RemoteMediator<Int, PlaylistSearchResult>() {

    override suspend fun initialize(): InitializeAction {
        val cacheTimeout = TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS)

        return if (System.currentTimeMillis() - (db.playlistPageKeysDao().getCachingTimestamp() ?: 0) < cacheTimeout) {
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PlaylistSearchResult>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH ->  {
                /*
                    If remoteKey is not null, then we can get the nextKey from it. In the Github API the page keys are incremented sequentially. So to get the page that contains the current item, we just subtract 1 from remoteKey.nextKey.
                    If RemoteKey is null (because the anchorPosition was null), then the page we need to load is the initial one: GITHUB_STARTING_PAGE_INDEX
                 */
                val remoteKeys = getKeysClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: START_SYNTHETIC_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getKeysForFirstItem(state)
                // If remoteKeys is null, that means the refresh result is not in the database yet.
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getKeysForLastItem(state)
                // If remoteKeys is null, that means the refresh result is not in the database yet.
                // We can return Success with endOfPaginationReached = false because Paging
                // will call this method again if RemoteKeys becomes non-null.
                // If remoteKeys is NOT NULL but its nextKey is null, that means we've reached
                // the end of pagination for append.
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)

                nextKey
            }
        }

        val limit = state.config.pageSize
        val offset = page * limit

        try {
            val apiResponse = api.getMyPlaylists(limit, offset)
            if (apiResponse is NetworkResponse.Error) {
                return MediatorResult.Error(apiResponse.error ?: IOException("Unknown error MyPlaylistsMediator.load():68"))
            }

            val successfulResponse = apiResponse as NetworkResponse.Success
            val playlists = successfulResponse.body.items
            val endOfPaginationReached = playlists.isEmpty()

            db.withTransaction {

                if (loadType == LoadType.REFRESH) {
                    // if reloading all, clear all from db
                    db.playlistPageKeysDao().clearKeys()
                    db.playlistDao().clearAll()
                }

                val prevKey = if (page == START_SYNTHETIC_PAGE_INDEX) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1

                val keys = playlists.map {
                    PlaylistPageKeys(playlistId = it.id, prevKey = prevKey, nextKey = nextKey)
                }

                db.playlistPageKeysDao().insertAll(keys)
                db.playlistDao().insertAll(playlists.map { it.toDomainObject() })

            }

            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getKeysForLastItem(state: PagingState<Int, PlaylistSearchResult>): PlaylistPageKeys? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { repo ->
                // Get the remote keys of the last item retrieved
                db.playlistPageKeysDao().keysByPlaylistId(repo.id)
            }
    }

    private suspend fun getKeysForFirstItem(state: PagingState<Int, PlaylistSearchResult>): PlaylistPageKeys? {
        // Get the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { repo ->
                // Get the remote keys of the first item retrieved
                db.playlistPageKeysDao().keysByPlaylistId(repo.id)
            }
    }

    private suspend fun getKeysClosestToCurrentPosition(state: PagingState<Int, PlaylistSearchResult>): PlaylistPageKeys? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { repoId ->
                db.playlistPageKeysDao().keysByPlaylistId(repoId)
            }
        }
    }

}