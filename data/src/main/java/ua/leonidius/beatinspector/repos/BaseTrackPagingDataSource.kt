package ua.leonidius.beatinspector.repos

import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import ua.leonidius.beatinspector.datasources.cache.InMemCache
import ua.leonidius.beatinspector.datasources.network.dto.ErrorResponse
import ua.leonidius.beatinspector.datasources.network.mappers.ListMapper
import ua.leonidius.beatinspector.entities.SongSearchResult

open class BaseTrackPagingDataSource<D: ListMapper<SongSearchResult>>(
    api: suspend (limit: Int, offset: Int) -> NetworkResponse<D, ErrorResponse>,
    cache: InMemCache<String, SongSearchResult>,
    hideExplicit: Flow<Boolean>,
): BasePagingDataSourceWithTitleCache<SongSearchResult, D>(
    api, cache,
    filter = { song -> !hideExplicit.first() || !song.isExplicit }
)