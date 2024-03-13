package ua.leonidius.beatinspector.features.home.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import dagger.hilt.android.lifecycle.HiltViewModel
import ua.leonidius.beatinspector.BeatInspectorApp
import ua.leonidius.beatinspector.data.shared.PagingDataSource
import ua.leonidius.beatinspector.data.playlists.domain.PlaylistSearchResult
import ua.leonidius.beatinspector.data.account.repository.AccountRepository
import ua.leonidius.beatinspector.shared.viewmodels.AccountImageViewModel
import ua.leonidius.beatinspector.shared.viewmodels.AccountImageViewModelImpl
import javax.inject.Inject

/**
 * ViewModel for the main screen, the one with user's playlists.
 */
@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    accountRepository: AccountRepository,
    myPlaylistsPagingDataSource: PagingDataSource<PlaylistSearchResult>,
): ViewModel(), AccountImageViewModel by AccountImageViewModelImpl(accountRepository) {

    val playlistsPagingFlow = myPlaylistsPagingDataSource.getFlow(viewModelScope)

    init {
        loadAccountImage(viewModelScope)
    }

    /*companion object {

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {

            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val app = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as BeatInspectorApp

                return HomeScreenViewModel(
                    app.accountRepository,
                    app.myPlaylistsPagingDataSource,
                ) as T
            }

        }

    }*/

}