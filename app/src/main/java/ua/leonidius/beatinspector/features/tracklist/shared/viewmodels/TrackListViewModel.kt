package ua.leonidius.beatinspector.features.tracklist.shared.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ua.leonidius.beatinspector.data.shared.PagingDataSource
import ua.leonidius.beatinspector.data.tracks.shared.domain.SongSearchResult

abstract class TrackListViewModel(
    pagingSource: PagingDataSource<SongSearchResult>,
): ViewModel() {

    val flow = pagingSource.getFlow(viewModelScope)

}