package ua.leonidius.beatinspector.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.launch
import ua.leonidius.beatinspector.BeatInspectorApp
import ua.leonidius.beatinspector.R
import ua.leonidius.beatinspector.SongDataIOException
import ua.leonidius.beatinspector.entities.SongSearchResult
import ua.leonidius.beatinspector.repos.SongsRepository
import ua.leonidius.beatinspector.repos.SongsRepositoryImpl

class SearchViewModel(
    private val songsRepository: SongsRepository
) : ViewModel() {

    enum class UiState {
        UNINITIALIZED,
        LOADING,
        LOADED,
        ERROR
    }

    var uiState by mutableStateOf(UiState.UNINITIALIZED)
        private set

    var errorMessageId by mutableIntStateOf(-1)
        private set

    var query by mutableStateOf("")
    private val _searchResults = mutableStateOf(emptyList<SongSearchResult>())

    val searchResults: List<SongSearchResult>
        get() = _searchResults.value

    fun performSearch() {
        viewModelScope.launch {
            try {
                uiState = UiState.LOADING
                _searchResults.value = songsRepository.searchForSongsByTitle(query)
                uiState = UiState.LOADED
            } catch (e: SongsRepositoryImpl.NotAuthedError) {
                uiState = UiState.ERROR
                errorMessageId = R.string.other_error // todo: check how that is thrown, handle it differently so that it redirects to login page
                // todo: set this to AuthStatusViewModel and initiate login

            } catch (e: SongDataIOException) {
                uiState = UiState.ERROR
                errorMessageId = when(e.type) {
                    SongDataIOException.Type.NETWORK -> {
                        R.string.network_error
                    }

                    SongDataIOException.Type.SERVER -> {
                        R.string.server_error
                    }

                    SongDataIOException.Type.UNKNOWN -> {
                        R.string.unknown_error
                    }

                    SongDataIOException.Type.OTHER -> {
                        R.string.other_error
                    }
                }
            } catch (e: Error) {
                uiState = UiState.ERROR
                Log.e("SearchViewModel", "Unknown error", e)
                errorMessageId = R.string.unknown_error
            }

        }
    }

    companion object {

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {

            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val app = checkNotNull(extras[APPLICATION_KEY]) as BeatInspectorApp

                return SearchViewModel(app.songsRepository) as T
            }

        }

    }


}