package ua.leonidius.beatinspector.data.shared.repository

import androidx.paging.PagingData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ua.leonidius.beatinspector.data.shared.BasePagingDataSourceWithTitleCache
import ua.leonidius.beatinspector.data.shared.ListMapper
import ua.leonidius.beatinspector.data.tracks.lists.BaseTrackPagingDataSource
import ua.leonidius.beatinspector.data.tracks.lists.liked.network.dto.LikedTracksResponse
import ua.leonidius.beatinspector.data.tracks.shared.domain.SongSearchResult
import ua.leonidius.beatinspector.shared.domain.SettingsState

/**
 * @param P - PagingSource type
 */
abstract class BaseTrackListPagingRepository {

    abstract val items: Flow<PagingData<SongSearchResult>>

    protected abstract fun createPagingSource():
            BaseTrackPagingDataSource<LikedTracksResponse>

}