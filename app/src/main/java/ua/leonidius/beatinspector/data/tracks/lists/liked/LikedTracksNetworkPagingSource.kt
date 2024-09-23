package ua.leonidius.beatinspector.data.tracks.lists.liked

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import ua.leonidius.beatinspector.data.shared.cache.Cache
import ua.leonidius.beatinspector.data.shared.network.dto.ErrorResponse
import ua.leonidius.beatinspector.data.shared.network.toUIException
import ua.leonidius.beatinspector.data.tracks.lists.liked.network.api.LikedTracksApi
import ua.leonidius.beatinspector.data.tracks.lists.liked.network.dto.LikedTracksResponse
import ua.leonidius.beatinspector.data.tracks.shared.cache.TrackBaseDetailsDbDataSource
import ua.leonidius.beatinspector.data.tracks.shared.domain.SongSearchResult
import ua.leonidius.beatinspector.shared.domain.SettingsState
import javax.inject.Inject

class LikedTracksNetworkPagingSource @Inject constructor(
    service: LikedTracksApi,
    searchCache: TrackBaseDetailsDbDataSource,
    settingsFlow: Flow<SettingsState>,
) : PagingSource<Int, SongSearchResult>() {

    private val service: suspend (limit: Int, offset: Int) -> NetworkResponse<LikedTracksResponse, ErrorResponse> =
        service::getSavedTracks
    private val cache: Cache<String, SongSearchResult> = searchCache
    private val filter: (suspend (SongSearchResult) -> Boolean)? =
        { song -> !settingsFlow.map { it.hideExplicit }.first() || !song.isExplicit }

    private val itemsPerPage = 50

    companion object {
        const val ITEMS_PER_PAGE = 50
    }

    override fun getRefreshKey(state: PagingState<Int, SongSearchResult>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SongSearchResult> {
        val page = params.key ?: 1

        val offset = (page - 1) * itemsPerPage

        when (val resp = service(itemsPerPage, offset)) {
            is NetworkResponse.Success -> {

                var trackList = resp.body.toDomainObject()

                filter?.let { filter ->
                    trackList = trackList.filter { filter(it) }
                }

                cache.batchAdd(trackList.associateBy { it.id })

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

