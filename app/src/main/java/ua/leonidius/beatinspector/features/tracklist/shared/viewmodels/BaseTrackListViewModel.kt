package ua.leonidius.beatinspector.features.tracklist.shared.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ua.leonidius.beatinspector.data.shared.PagingDataSource
import ua.leonidius.beatinspector.data.tracks.shared.domain.SongSearchResult

interface TrackListViewModel {

    val flow: Flow<PagingData<SongSearchResult>>

}

abstract class BaseTrackListViewModel(
    pagingSource: PagingDataSource<SongSearchResult>,
): ViewModel(), TrackListViewModel {

    override val flow = pagingSource.getFlow(viewModelScope)

}