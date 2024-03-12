package ua.leonidius.beatinspector.data.tracks.lists

import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import ua.leonidius.beatinspector.data.shared.cache.InMemCache
import ua.leonidius.beatinspector.data.shared.network.dto.ErrorResponse
import ua.leonidius.beatinspector.data.shared.ListMapper
import ua.leonidius.beatinspector.data.tracks.shared.domain.SongSearchResult
import ua.leonidius.beatinspector.data.shared.BasePagingDataSourceWithTitleCache

open class BaseTrackPagingDataSource<D: ListMapper<SongSearchResult>>(
    api: suspend (limit: Int, offset: Int) -> NetworkResponse<D, ErrorResponse>,
    cache: InMemCache<String, SongSearchResult>,
    hideExplicit: Flow<Boolean>,
): BasePagingDataSourceWithTitleCache<SongSearchResult, D>(
    api, cache,
    filter = { song -> !hideExplicit.first() || !song.isExplicit }
)