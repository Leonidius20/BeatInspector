package ua.leonidius.beatinspector.features.tracklist.recent.viewmodels

import dagger.hilt.android.lifecycle.HiltViewModel
import ua.leonidius.beatinspector.data.shared.PagingDataSource
import ua.leonidius.beatinspector.data.tracks.shared.domain.SongSearchResult
import ua.leonidius.beatinspector.features.tracklist.shared.viewmodels.TrackListViewModel
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class RecentlyPlayedViewModel @Inject constructor(
    @Named("recent") pagingSource: PagingDataSource<SongSearchResult>,
): TrackListViewModel(pagingSource)