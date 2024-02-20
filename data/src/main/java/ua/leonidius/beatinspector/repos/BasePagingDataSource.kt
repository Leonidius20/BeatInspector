package ua.leonidius.beatinspector.repos

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
import ua.leonidius.beatinspector.datasources.network.dto.ErrorResponse
import ua.leonidius.beatinspector.datasources.network.mappers.ListMapper
import ua.leonidius.beatinspector.toUIException

// todo: db cache
/**
 * @param D - dto type
 */
abstract class BasePagingDataSource<D: ListMapper<T>, T : Any>(
    private val service: suspend (limit: Int, offset: Int) -> NetworkResponse<D, ErrorResponse>,
): PagingSource<Int, T>(), PagingDataSource<T> {

    private val itemsPerPage = 50

    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        val page = params.key ?: 1

        val offset = (page - 1) * itemsPerPage

        when (val resp = service(itemsPerPage, offset)) {
            is NetworkResponse.Success -> {

                val trackList = resp.body.toDomainObject()

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

    override fun getFlow(scope: CoroutineScope): Flow<PagingData<T>> {
        return Pager(PagingConfig(pageSize = itemsPerPage)) {
            this
        }.flow.cachedIn(scope)
    }

}