package ua.leonidius.beatinspector.data.tracks.lists.recent

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.cachedIn
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import ua.leonidius.beatinspector.data.shared.PagingDataSource
import ua.leonidius.beatinspector.data.tracks.shared.cache.SongTitlesInMemCache
import ua.leonidius.beatinspector.data.tracks.lists.recent.network.api.RecentlyPlayedApi
import ua.leonidius.beatinspector.data.tracks.shared.domain.SongSearchResult
import ua.leonidius.beatinspector.data.shared.network.toUIException

class RecentlyPlayedDataSource(
    private val service: RecentlyPlayedApi,
    private val searchCache: SongTitlesInMemCache,
    private val hideExplicit: Flow<Boolean>,
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


                if (hideExplicit.first()) {
                    trackList = trackList.filter { !it.isExplicit }
                }

                searchCache.batchAdd(trackList.associateBy { it.id })
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