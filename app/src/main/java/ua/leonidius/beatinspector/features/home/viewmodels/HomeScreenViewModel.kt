package ua.leonidius.beatinspector.features.home.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import ua.leonidius.beatinspector.data.playlists.repository.MyPlaylistsRepository
import javax.inject.Inject

/**
 * ViewModel for the main screen, the one with user's playlists.
 */
@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    myPlaylistsRepository: MyPlaylistsRepository,
): ViewModel() {

    val playlistsPagingFlow = myPlaylistsRepository.getMyPlaylistsFlow()
        .cachedIn(viewModelScope)

}