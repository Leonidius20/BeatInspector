package ua.leonidius.beatinspector.data.tracks.lists.liked.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.haroldadmin.cnradapter.NetworkResponse
import retrofit2.HttpException
import ua.leonidius.beatinspector.data.shared.db.TracksDatabase
import ua.leonidius.beatinspector.data.tracks.lists.liked.db.entities.LikedTrackWithPageKeys
import ua.leonidius.beatinspector.data.tracks.lists.liked.network.api.LikedTracksApi
import ua.leonidius.beatinspector.data.tracks.shared.cache.TrackBaseDetailsDbDataSource
import ua.leonidius.beatinspector.data.tracks.shared.db.TrackBaseDetails
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalPagingApi::class)
@Singleton
class LikedTracksRemoteMediator @Inject constructor(
    private val api: LikedTracksApi,
    private val db: TracksDatabase,
    private val trackBaseDetailsDbDataSource: TrackBaseDetailsDbDataSource,
) : RemoteMediator<Int, TrackBaseDetails>() {

    private val likedTracksDao = db.likedTracksDao()

    companion object {
        // cache is valid only for 30 minutes.
        private val cacheLifeTime = TimeUnit.MILLISECONDS
            .convert(30, TimeUnit.MINUTES)

        /**
         * Spotify API doesn't actually work with page numbers, instead
         * it works with offsets and limits, we just pretend to work with pages
         */
        private const val FIRST_PAGE_NUMBER = 0
    }

    override suspend fun initialize(): InitializeAction {
        return if (System.currentTimeMillis() - (likedTracksDao.getCachingTimestamp()
                ?: 0) < cacheLifeTime
        ) {
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

        val limit = state.config.pageSize
        val offset = page * limit

        try {
            val response = api.getSavedTracks(limit = limit, offset = offset)

            if (response is NetworkResponse.Error) {
                return MediatorResult.Error(
                    response.error ?: IOException("Unknown error in LikedTracksRemoteMediator")
                )
            }

            val successfulResponse = response as NetworkResponse.Success

            val loadedLikedTracks = successfulResponse.body.items
            val endOfPaginationReached = loadedLikedTracks.isEmpty()

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    likedTracksDao.clearAll() // this also clears keys as they are in same table
                }

                val prevKey = if (page == FIRST_PAGE_NUMBER) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val cachedAt = System.currentTimeMillis()

                likedTracksDao.insertAll(
                    loadedLikedTracks.map { track ->
                        LikedTrackWithPageKeys(
                            trackId = track.track.id,
                            prevKey = prevKey,
                            nextKey = nextKey,
                            cachedAt = cachedAt,
                        )
                    }
                )

                trackBaseDetailsDbDataSource.batchAdd(
                    loadedLikedTracks.map { it.track.toDomainObject() }
                )
            }

            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        }

        TODO("Not yet implemented")
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, TrackBaseDetails>): LikedTrackWithPageKeys? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { track ->
                // Get the remote keys of the last item retrieved
                likedTracksDao.getById(track.trackId)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, TrackBaseDetails>): LikedTrackWithPageKeys? {
        // Get the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { track ->
                // Get the remote keys of the first items retrieved
                likedTracksDao.getById(track.trackId)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, TrackBaseDetails>
    ): LikedTrackWithPageKeys? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.let { track ->
                likedTracksDao.getById(track.trackId)
            }
        }
    }

}