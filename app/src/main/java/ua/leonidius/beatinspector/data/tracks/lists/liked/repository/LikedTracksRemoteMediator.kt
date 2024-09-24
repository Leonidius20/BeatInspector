package ua.leonidius.beatinspector.data.tracks.lists.liked.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import ua.leonidius.beatinspector.data.tracks.lists.liked.db.LikedTracksDbDataSource
import ua.leonidius.beatinspector.data.tracks.lists.liked.db.entities.LikedTrackWithPageKeys
import ua.leonidius.beatinspector.data.tracks.lists.liked.network.LikedTracksNetworkDataSource
import ua.leonidius.beatinspector.data.tracks.shared.db.TrackBaseDetails
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalPagingApi::class)
@Singleton
class LikedTracksRemoteMediator @Inject constructor(
    private val remoteSource: LikedTracksNetworkDataSource,
    private val dbDataSource: LikedTracksDbDataSource,
) : RemoteMediator<Int, TrackBaseDetails>() {

    companion object {

        /**
         * Spotify API doesn't actually work with page numbers, instead
         * it works with offsets and limits, we just pretend to work with pages
         */
        private const val FIRST_PAGE_NUMBER = 0
    }

    override suspend fun initialize(): InitializeAction {
        return if (dbDataSource.isCacheFresh()) {
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, TrackBaseDetails>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: FIRST_PAGE_NUMBER
            }

            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                // If remoteKeys is null, that means the refresh result is not in the database yet.
                val prevKey = remoteKeys?.prevKey
                if (prevKey == null) {
                    return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                }
                prevKey
            }

            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                // If remoteKeys is null, that means the refresh result is not in the database yet.
                // We can return Success with endOfPaginationReached = false because Paging
                // will call this method again if RemoteKeys becomes non-null.
                // If remoteKeys is NOT NULL but its nextKey is null, that means we've reached
                // the end of pagination for append.
                val nextKey = remoteKeys?.nextKey
                if (nextKey == null) {
                    return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                }
                nextKey
            }
        }

        try {
            val loadedLikedTracks = remoteSource.getLikedTracks(
                page, itemsPerPage = state.config.pageSize
            ) // throws SongDataIOException

            val endOfPaginationReached = loadedLikedTracks.isEmpty()

            val shouldClearExisting = loadType == LoadType.REFRESH

            val prevKey = if (page == FIRST_PAGE_NUMBER) null else page - 1
            val nextKey = if (endOfPaginationReached) null else page + 1

            dbDataSource.insertPage(
                data = loadedLikedTracks,
                clearExisting = shouldClearExisting,
                prevPageKey = prevKey,
                nextPageKey = nextKey
            )

            return MediatorResult.Success(
                endOfPaginationReached = endOfPaginationReached
            )

        } catch (e: IOException) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, TrackBaseDetails>): LikedTrackWithPageKeys? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { track ->
                // Get the remote keys of the last item retrieved
                dbDataSource.getItemWithPageKeysById(track.trackId)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, TrackBaseDetails>): LikedTrackWithPageKeys? {
        // Get the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { track ->
                // Get the remote keys of the first items retrieved
                dbDataSource.getItemWithPageKeysById(track.trackId)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, TrackBaseDetails>
    ): LikedTrackWithPageKeys? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.let { track ->
                dbDataSource.getItemWithPageKeysById(track.trackId)
            }
        }
    }

}