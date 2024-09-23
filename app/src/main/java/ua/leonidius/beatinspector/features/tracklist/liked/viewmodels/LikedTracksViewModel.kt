package ua.leonidius.beatinspector.features.tracklist.liked.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import ua.leonidius.beatinspector.data.tracks.lists.liked.repository.LikedTracksRepository
import ua.leonidius.beatinspector.features.tracklist.shared.viewmodels.TrackListViewModel
import javax.inject.Inject

@HiltViewModel
class LikedTracksViewModel @Inject constructor(
    likedTracksRepository: LikedTracksRepository,
): ViewModel(), TrackListViewModel {

    override val flow = likedTracksRepository
        .getLikedTracksPagedFlow()
        .cachedIn(viewModelScope)

}