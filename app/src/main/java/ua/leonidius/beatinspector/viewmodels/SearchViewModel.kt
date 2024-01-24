package ua.leonidius.beatinspector.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.launch
import ua.leonidius.beatinspector.BeatInspectorApp
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

    var errorMessage by mutableStateOf("")
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
                errorMessage = "Not authed" // todo: check how that is thrown and extract string res
                // todo: set this to AuthStatusViewModel and initiate login

            } catch (e: SongDataIOException) {
                uiState = UiState.ERROR
                errorMessage = when(e.type) {
                    SongDataIOException.Type.NETWORK -> {
                        "Network error. Check your connection." // todo: extract string res
                    }

                    SongDataIOException.Type.SERVER -> {
                        "Server error"
                    }

                    SongDataIOException.Type.UNKNOWN -> {
                        "Unknown error"
                    }

                    SongDataIOException.Type.OTHER -> {
                        "Other error"
                    }
                }

                e.message?.also {
                    errorMessage += ": $it"
                }
            } catch (e: Error) {
                uiState = UiState.ERROR
                errorMessage = e.message ?: "Unknown error"
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