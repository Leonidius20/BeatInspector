package ua.leonidius.beatinspector.features.home.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import ua.leonidius.beatinspector.BeatInspectorApp
import ua.leonidius.beatinspector.PagingDataSource
import ua.leonidius.beatinspector.entities.PlaylistSearchResult
import ua.leonidius.beatinspector.repos.account.AccountRepository
import ua.leonidius.beatinspector.shared.viewmodels.AccountImageViewModel
import ua.leonidius.beatinspector.shared.viewmodels.AccountImageViewModelImpl

/**
 * ViewModel for the main screen, the one with user's playlists.
 */
class HomeScreenViewModel(
    accountRepository: AccountRepository,
    myPlaylistsPagingDataSource: PagingDataSource<PlaylistSearchResult>,
): ViewModel(), AccountImageViewModel by AccountImageViewModelImpl(accountRepository) {

    val playlistsPagingFlow = myPlaylistsPagingDataSource.getFlow(viewModelScope)

    init {
        loadAccountImage(viewModelScope)
    }

    companion object {

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

    }

}