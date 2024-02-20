package ua.leonidius.beatinspector.repos.saved_tracks

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.haroldadmin.cnradapter.NetworkResponse
import ua.leonidius.beatinspector.datasources.cache.SearchCacheDataSource
import ua.leonidius.beatinspector.datasources.network.mappers.toDomainObject
import ua.leonidius.beatinspector.datasources.network.services.SavedTracksService
import ua.leonidius.beatinspector.entities.SongSearchResult
import ua.leonidius.beatinspector.toUIException

class SavedTracksNetworkPagingSource(
    private val service: SavedTracksService,
    private val searchCache: SearchCacheDataSource,
): PagingSource<Int, SongSearchResult>() {

    val itemsPerPage = 50

    override fun getRefreshKey(state: PagingState<Int, SongSearchResult>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SongSearchResult> {
        val page = params.key ?: 1

        val offset = (page - 1) * itemsPerPage

        when (val resp = service.getSavedTracks(itemsPerPage, offset)) {
            is NetworkResponse.Success -> {

                val trackList = resp.body.items.map { it.track.toDomainObject() }
                searchCache.updateCache("", trackList)
                return LoadResult.Page(
                    data = trackList,
                    // prevKey = if (page == 1) null else page - 1,
                    prevKey = null,
                    nextKey = if (trackList.isEmpty()) null else page + 1
                )

            }
            is NetworkResponse.Error -> {
                return LoadResult.Error(resp.toUIException())
            }
        }

    }


}