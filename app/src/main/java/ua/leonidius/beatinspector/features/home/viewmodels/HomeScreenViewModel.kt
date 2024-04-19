package ua.leonidius.beatinspector.features.home.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import ua.leonidius.beatinspector.data.account.repository.AccountRepository
import ua.leonidius.beatinspector.data.playlists.repository.MyPlaylistsRepository
import ua.leonidius.beatinspector.shared.viewmodels.AccountImageViewModel
import ua.leonidius.beatinspector.shared.viewmodels.AccountImageViewModelImpl
import javax.inject.Inject

/**
 * ViewModel for the main screen, the one with user's playlists.
 */
@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    accountRepository: AccountRepository,
    myPlaylistsRepository: MyPlaylistsRepository,
): ViewModel(), AccountImageViewModel by AccountImageViewModelImpl(accountRepository) {

    val playlistsPagingFlow = myPlaylistsRepository.getMyPlaylistsFlow()
        .cachedIn(viewModelScope)

    init {
        loadAccountImage(viewModelScope)
    }

}