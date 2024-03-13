package ua.leonidius.beatinspector.features.tracklist.top.viewmodels

import dagger.hilt.android.lifecycle.HiltViewModel
import ua.leonidius.beatinspector.data.shared.PagingDataSource
import ua.leonidius.beatinspector.data.tracks.shared.domain.SongSearchResult
import ua.leonidius.beatinspector.features.tracklist.shared.viewmodels.TrackListViewModel
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class TopTracksViewModel @Inject constructor(
    @Named("top") pagingSource: PagingDataSource<SongSearchResult>,
): TrackListViewModel(pagingSource)