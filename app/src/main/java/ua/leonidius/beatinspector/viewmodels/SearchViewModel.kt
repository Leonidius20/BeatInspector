package ua.leonidius.beatinspector.viewmodels

import android.util.Log
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
import ua.leonidius.beatinspector.R
import ua.leonidius.beatinspector.SongDataIOException
import ua.leonidius.beatinspector.entities.SongSearchResult
import ua.leonidius.beatinspector.repos.SongsRepository
import ua.leonidius.beatinspector.repos.SongsRepositoryImpl

class SearchViewModel(
    private val songsRepository: SongsRepository
) : ViewModel() {

    sealed class UiState {

        object Uninitialized : UiState()

        object Loading : UiState()

        data class Loaded(
            val searchResults: List<SongSearchResult>
        ) : UiState()

        data class Error(
            val errorMessageId: Int
        ) : UiState()

    }

    var query by mutableStateOf("")

    var uiState by mutableStateOf<UiState>(UiState.Uninitialized)
        private set

    //val searchQueriesFlow = MutableSharedFlow<String>() // todoL should it be mutable

    ///init {
    //    searchQueriesFlow.collect { query ->
            // lauch a coroutine to perform search
            // cancel previous request if it is still running
            // should we be cancelling a coroutine ?
            // no, only not letting next requests happen,
            // but how do we know that the prev is still running?
            // how do we combine network coroutines with this flow though
     ///   }
    //}

    fun performSearch() {
        //searchQueriesFlow.emit(query)

        viewModelScope.launch {
            try {
                uiState = UiState.Loading
                val results = songsRepository.searchForSongsByTitle(query)
                uiState = UiState.Loaded(results)
            } catch (e: SongsRepositoryImpl.NotAuthedError) {
                // todo: check how that is thrown, handle it differently so that it redirects to login page
                // todo: set this to AuthStatusViewModel and initiate login

                uiState = UiState.Error(R.string.other_error)
            } catch (e: SongDataIOException) {
                uiState = UiState.Error(e.toUiMessage())
            } catch (e: Error) {
                uiState = UiState.Error(R.string.unknown_error)
                Log.e("SearchViewModel", "Unknown error", e)
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