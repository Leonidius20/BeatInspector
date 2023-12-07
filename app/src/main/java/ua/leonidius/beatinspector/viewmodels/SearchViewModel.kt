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
import ua.leonidius.beatinspector.domain.SearchSongsUseCase
import ua.leonidius.beatinspector.domain.entities.SongSearchResult

class SearchViewModel(
    val searchSongsUseCase: SearchSongsUseCase
) : ViewModel() {

    var query by mutableStateOf("")
    private val _searchResults = mutableStateOf(emptyList<SongSearchResult>())

    val searchResults: List<SongSearchResult>
        get() = _searchResults.value

    fun performSearch() {
        viewModelScope.launch {
            _searchResults.value = searchSongsUseCase.searchSongs(query)
        }
    }

    companion object {

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {

            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val app = checkNotNull(extras[APPLICATION_KEY]) as BeatInspectorApp

                return SearchViewModel(app.searchSongsUseCase) as T
            }

        }

    }


}