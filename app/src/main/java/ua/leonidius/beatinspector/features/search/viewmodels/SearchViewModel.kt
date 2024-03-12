package ua.leonidius.beatinspector.features.search.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.launch
import ua.leonidius.beatinspector.BeatInspectorApp
import ua.leonidius.beatinspector.R
import ua.leonidius.beatinspector.data.shared.exception.SongDataIOException
import ua.leonidius.beatinspector.data.tracks.shared.domain.SongSearchResult
import ua.leonidius.beatinspector.data.account.repository.AccountRepository
import ua.leonidius.beatinspector.data.tracks.search.repository.SearchRepository
import ua.leonidius.beatinspector.data.tracks.search.repository.SearchRepositoryImpl
import ua.leonidius.beatinspector.shared.viewmodels.AccountImageViewModel
import ua.leonidius.beatinspector.shared.viewmodels.AccountImageViewModelImpl
import ua.leonidius.beatinspector.shared.uimapping.toUiMessage

class SearchViewModel(
    savedStateHandle: SavedStateHandle,
    private val searchRepository: SearchRepository,
    accountRepository: AccountRepository,
) : ViewModel(), AccountImageViewModel by AccountImageViewModelImpl(accountRepository) {

    sealed class UiState {

        object Uninitialized : UiState()

        object Loading : UiState()

        data class Loaded(
            val searchResults: List<SongSearchResult>,
        ) : UiState()

        data class Error(
            val errorMessageId: Int,
            val errorAdditionalInfo: String,
        ) : UiState()

    }


    var query by mutableStateOf(savedStateHandle["query"] ?: "")

    var uiState by mutableStateOf<UiState>(UiState.Uninitialized)
        private set

    init {
        if (query.isNotEmpty()) {
            performSearch()
        }
        loadAccountImage(viewModelScope)
    }



    fun performSearch() {
        //searchQueriesFlow.emit(query)

        viewModelScope.launch {
            try {
                uiState = UiState.Loading
                val results = searchRepository.get(query)
                uiState = UiState.Loaded(results)
            } catch (e: SearchRepositoryImpl.NotAuthedError) {
                // todo: check how that is thrown, handle it differently so that it redirects to login page
                // todo: set this to AuthStatusViewModel and initiate login

                uiState = UiState.Error(
                    R.string.other_error,
                    """
                        NotAuthedError thrown:
                        Message: ${e.message}
                        Try logging out (or clearing app data) and logging in again.
                    """.trimIndent()
                )
            } catch (e: SongDataIOException) {
                uiState = UiState.Error(
                    e.toUiMessage(),
                    e.toTextDescription()
                )
            } catch (e: Error) {
                uiState = UiState.Error(
                    R.string.unknown_error,
                    """
                        Non-SongDataIOException exception thrown:
                        Type: ${e.javaClass.name}
                        ${e.message}
                    """.trimIndent()
                )
                Log.e("SearchViewModel", "Unknown error", e)
            }

        }
    }

    /**
     * Return to the list of user's playlists, hide the search results
     */
    fun returnToUninitialized() {
        uiState = UiState.Uninitialized
    }

    companion object {

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {

            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val app = checkNotNull(extras[APPLICATION_KEY]) as BeatInspectorApp

                return SearchViewModel(
                    extras.createSavedStateHandle(),
                    app.searchRepository,
                    app.accountRepository,
                ) as T
            }

        }

    }


}