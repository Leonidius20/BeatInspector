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
import ua.leonidius.beatinspector.entities.SongSearchResult
import ua.leonidius.beatinspector.repos.SongsRepository
import ua.leonidius.beatinspector.repos.SongsRepositoryImpl

class SearchViewModel(
    private val songsRepository: SongsRepository
) : ViewModel() {

    var query by mutableStateOf("")
    private val _searchResults = mutableStateOf(emptyList<SongSearchResult>())

    val searchResults: List<SongSearchResult>
        get() = _searchResults.value

    fun performSearch() {
        viewModelScope.launch {
            try {
                _searchResults.value = songsRepository.searchForSongsByTitle(query)
            } catch (e: SongsRepositoryImpl.NotAuthedError) {
                // todo: set this to AuthStatusViewModel and initiate login
                e.printStackTrace()
            } catch (e: Error) {
                // todo: proper handling
                e.printStackTrace()
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