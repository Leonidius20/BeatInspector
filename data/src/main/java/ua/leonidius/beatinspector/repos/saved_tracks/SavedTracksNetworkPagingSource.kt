package ua.leonidius.beatinspector.repos.saved_tracks

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.cachedIn
import ua.leonidius.beatinspector.datasources.cache.SearchCacheDataSource
import ua.leonidius.beatinspector.datasources.network.SavedTracksNetworkDataSource
import ua.leonidius.beatinspector.entities.SongSearchResult

class SavedTracksNetworkPagingSource(
    private val networkDataSource: SavedTracksNetworkDataSource,
    private val searchCache: SearchCacheDataSource,
): PagingSource<Int, SongSearchResult>() {

    override fun getRefreshKey(state: PagingState<Int, SongSearchResult>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SongSearchResult> {
        val page = params.key ?: 1
        try {
            val response = networkDataSource.get(page)
            searchCache.updateCache("", response)
            return LoadResult.Page(
                data = response,
                // prevKey = if (page == 1) null else page - 1,
                prevKey = null,
                nextKey = if (response.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }

    }


}