package ua.leonidius.beatinspector.repos.recently_played

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.cachedIn
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import ua.leonidius.beatinspector.PagingDataSource
import ua.leonidius.beatinspector.datasources.cache.SearchCacheDataSource
import ua.leonidius.beatinspector.datasources.network.services.RecentlyPlayedApi
import ua.leonidius.beatinspector.entities.SongSearchResult
import ua.leonidius.beatinspector.toUIException

class RecentlyPlayedDataSource(
    private val service: RecentlyPlayedApi,
    private val searchCache: SearchCacheDataSource,
    private val hideExplicit: () -> Boolean,
): PagingSource<String, SongSearchResult>(), PagingDataSource<SongSearchResult> {

    private val itemsPerPage = 50
    override fun getRefreshKey(state: PagingState<String, SongSearchResult>): String? {
        /*return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey ?: anchorPage?.nextKey // or null
        }*/
        return null
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, SongSearchResult> {
        val page = params.key // could be null

        when (val resp = service.getRecentlyPlayed(itemsPerPage, before = page)) {
            is NetworkResponse.Success -> {

                val dto = resp.body
                var trackList = dto.toDomainObject()

                if (hideExplicit()) {
                    trackList = trackList.filter { !it.isExplicit }
                }

                searchCache.updateCache("", trackList)
                return LoadResult.Page(
                    data = trackList,
                    prevKey = /*dto.cursors?.before*/ null, // todo fix paging
                    nextKey = /*dto.cursors?.after*/ null,
                )

            }
            is NetworkResponse.Error -> {
                return LoadResult.Error(resp.toUIException())
            }
        }
    }

    override fun getFlow(scope: CoroutineScope): Flow<PagingData<SongSearchResult>> {
        return Pager(PagingConfig(pageSize = itemsPerPage)) {
            this
        }.flow.cachedIn(scope)
    }


}